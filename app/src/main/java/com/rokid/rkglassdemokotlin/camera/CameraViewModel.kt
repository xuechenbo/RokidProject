package com.rokid.rkglassdemokotlin.camera

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.usb.UsbDevice
import android.util.Base64
import android.view.Surface
import android.view.TextureView
import androidx.lifecycle.MutableLiveData
import com.arcsoft.face.*
import com.arcsoft.face.enums.DetectFaceOrientPriority
import com.arcsoft.face.enums.DetectMode
import com.rokid.axr.phone.glasscamera.RKGlassCamera
import com.rokid.axr.phone.glasscamera.callback.OnGlassCameraConnectListener
import com.rokid.logger.RKLogger
import com.rokid.rkglassdemokotlin.base.BaseViewModel
import com.rokid.utils.ContextUtil.getApplicationContext
import java.io.ByteArrayOutputStream
import java.util.*


class CameraViewModel : BaseViewModel() {
    val imageStrLive: MutableLiveData<String> = MutableLiveData()
    val rectString: MutableLiveData<Rect> = MutableLiveData()
    val rectList: MutableLiveData<List<FaceInfo>> = MutableLiveData()
    val strName: MutableLiveData<Int> = MutableLiveData()
    private lateinit var model: CameraModel
    val faceEngine = FaceEngine()

    //surface to show preview
    private var surface: Surface? = null
    private var faceId: Int? = -1

    //Camera Connection listener, to open camera immediately when camera is connected.
    private val cameraConnectionListener: OnGlassCameraConnectListener by lazy {
        object : OnGlassCameraConnectListener {
            override fun onGlassCameraConnected(p0: UsbDevice?) {
                //logout camera information on screen when connection is created
                model.showCameraInfo.postValue(true)
                //enable to click preview button after connected to camera
                model.previewEnabled.postValue(true)
                RKGlassCamera.getInstance().openCamera()
            }

            override fun onGlassCameraDisconnected() {
                model.showCameraInfo.postValue(true)
                model.cameraInfo.postValue("Camera Lost!!")
                stopPreview()
                //disable preview button
                model.previewEnabled.postValue(false)
            }
        }
    }

    //初始化sdk
    fun initFaceSDK() {
        // 如下的组合，初始化的功能包含：人脸检测、人脸识别
        val initMask = FaceEngine.ASF_FACE_DETECT or FaceEngine.ASF_FACE_RECOGNITION
        val code = faceEngine.init(
            getApplicationContext(),
            DetectMode.ASF_DETECT_MODE_VIDEO,
            DetectFaceOrientPriority.ASF_OP_0_ONLY,
            5,
            initMask
        )
        if (code !== ErrorInfo.MOK) {
            model.fps.postValue("初始化失败: $code")
        } else {
            model.fps.postValue("初始化成功")
        }
    }

    /**
     * Get model
     * get data model binding with view.
     * @return [CameraModel]
     */
    fun getModel(): CameraModel {
        model = CameraModel { it, toDo ->
            when (it) {
                CameraAction.StartPreview -> {//want to start preview
                    startPreView()
                }
                CameraAction.StopPreview -> {//want to stop preview
                    RKGlassCamera.getInstance().stopPreview()
                }
                CameraAction.Captured -> {//after capture a picture
                    event.postValue(toDo)
                }
                CameraAction.Recorded -> {//after record a movie
                    event.postValue(toDo)
                }
            }
        }
        return model
    }


    /**
     * Start pre view
     * first of all make sure surface is not null.
     */
    private fun startPreView() {
        //初始化SDK
        initFaceSDK()
        surface?.let {
            RKGlassCamera.getInstance().startPreview(it, 1920, 1080)
        } ?: run {
            startPreView()
        }
    }

    var shownPages = 0
    var startTimeMillion: Long = 0

    /**
     * Start camera
     * This part is just to init camera and start connect with init function.
     */
    fun startCamera() {
        //previewWidth and previewHeight must be set within the supported sizes.
        //supported sizes( For Air Pro ):
        // 1920 * 1080     1280 * 720     640 * 480      800 * 600
        // 1024 * 768      1280 * 960     1600 * 1200    2048 * 1536
        // 2592 * 1944     3264 * 2448    3200 * 2400    2688 * 1512
        // 320 * 240
        RKGlassCamera.getInstance().init(cameraConnectionListener)
        RKGlassCamera.getInstance().setCameraCallback(object : RKGlassCamera.RokidCameraCallback {
            override fun onOpen() {//when camera is opened

            }

            override fun onClose() {
                //in this test nothing to do
            }

            override fun onStartPreview() {//when preview is started.
                model.showCameraInfo.postValue(null)
                //set a default exposure
                val exposure = RKGlassCamera.getInstance().exposure
                RKLogger.e("AE == $exposure")
                model.ae.postValue(exposure)
                //start fps count
                startTimeMillion = System.currentTimeMillis()
                shownPages = 0
                //set a default zoom level
                model.zoom.postValue(RKGlassCamera.getInstance().zoom)
                //Here, after preview is started, supported preview sizes can be got from RKGlassCamera.getInstance().supportedPreviewSizes
                RKGlassCamera.getInstance().supportedPreviewSizes?.forEach {
                    RKLogger.e("it = ${it.width}  *  ${it.height}")
                }
            }

            override fun onStopPreview() {
                //when stopped.
            }

            override fun onError(p0: Exception?) {
                //when some error.
                p0?.printStackTrace()
            }

            override fun onStartRecording() {

            }

            override fun onStopRecording() {

            }
        })
        RKGlassCamera.getInstance().addOnPreviewFrameListener { nv21, timeStamp ->
            shownPages++
            val current = System.currentTimeMillis()
            val fps = ((shownPages * 1000) / (current - startTimeMillion))
            //FaceSDK
            val faceInfoList: List<FaceInfo> = ArrayList()
            val code = faceEngine.detectFaces(
                nv21,
                1920,
                1080,
                FaceEngine.CP_PAF_NV21,
                faceInfoList
            )
            //图片质量检测
//            val imageQualitySimilar = ImageQualitySimilar()
//            val imageQualityDetect = faceEngine.imageQualityDetect(
//                nv21,
//                1920,
//                1080,
//                FaceEngine.CP_PAF_BGR24,
//                faceInfoList.get(0),
//                0,
//                imageQualitySimilar
//            )
            if (code == ErrorInfo.MOK && faceInfoList.isNotEmpty()) {
                //TODO 如果数量为1 获取当前图像传给后台服务器
                //faceId 一张人脸从进入画面直到离开画面，faceId不变。
//                if (faceId != faceInfoList[0].faceId) {
//                    faceId = faceInfoList[0].faceId
//                    val nv21ToBitmap = NV21ToBitmap(
//                        appContext
//                    ).nv21ToBitmap(nv21, 1920, 1080)
//                    val bitmapToString = bitmapToBase64(nv21ToBitmap)
//                    imageStrLive.postValue(bitmapToString)
//                } else {
//                    //同一张人脸 return
//                }

                strName.postValue(faceInfoList.size)
//                rectString.postValue(faceInfoList[0].rect)
                rectList.postValue(faceInfoList)
            } else {
                strName.postValue(0)
//                rectString.postValue(Rect())
                rectList.postValue(faceInfoList)
            }
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return if (byteArrayOutputStream.toByteArray() == null) {
            null
        } else Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP)
    }

    fun getSurfaceListener(): TextureView.SurfaceTextureListener {
        return object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                p0: SurfaceTexture,
                p1: Int,
                p2: Int
            ) {
                surface = Surface(p0)
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
                //in this test nothing to do
            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                if (RKGlassCamera.getInstance().isRecording) {
                    RKGlassCamera.getInstance().stopRecord()
                }
                stopPreview()
                return true
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                //in this test nothing to do
            }

        }
    }

    private fun stopPreview() {
        try {
            RKGlassCamera.getInstance().stopPreview()
        } catch (e: Exception) {
        }
        surface = null
    }

    fun clearModel() {
        model.clearData()
    }
}
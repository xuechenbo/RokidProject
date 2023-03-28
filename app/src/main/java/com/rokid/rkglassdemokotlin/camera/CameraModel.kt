package com.rokid.rkglassdemokotlin.camera

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.rokid.axr.phone.glasscamera.RKGlassCamera
import com.rokid.logger.RKLogger
import com.rokid.rkglassdemokotlin.R
import com.rokid.rkglassdemokotlin.app.DeviceType
import com.rokid.rkglassdemokotlin.app.RKApplication
import com.rokid.rkglassdemokotlin.base.BaseEvent
import com.rokid.rkglassdemokotlin.databinding.ImgFullBinding
import com.rokid.rkglassdemokotlin.databinding.VideoplayerBinding
import com.rokid.rkglassdemokotlin.network.Result
import com.rokid.rkglassdemokotlin.network.ResultCallback
import com.rokid.rkglassdemokotlin.network.RetrofitNet
import com.rokid.rkglassdemokotlin.utils.MethodInputUtil
import com.rokid.uvc.usb.common.AbstractUVCCameraHandler
import com.rokid.uvc.usb.encoder.RecordParams
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File


/**
 * Camera action
 * action type in this view binding data
 */
enum class CameraAction {
    StartPreview, StopPreview, Captured, Recorded
}

/**
 * Camera model
 *
 * @property showCapture post true to show capture image.
 * @property showRecord post true to show record image.
 * @property previewEnabled after glass is connected, post true, default post false.
 * @property showCameraInfo show cameraInfo between glass is connected and preview is started.
 * @property showRecording post true to show record text
 * @property showMode post true when camera is connected.
 * @property showRecordingPoint to make a small animation about show/hide in 500ms
 * @property recordSrc image source of the ImageView shown when in screen mode.
 * @property isCapture if capture mode is checked this will post a true value
 * @property isRecord if record mode is checked this will post a true value
 * @property isAutoFocus if switcher is checked true/false this post a value true/false
 * @property showZoom if DeviceType is AirProPlus post null else post true
 * @property cameraInfo Information of glass camera
 * @property previewSrc text to show on preview button
 * @property zoom zoom size
 * @property ae ae size
 * @property action something to do
 */
data class CameraModel(
    val topHeight: MutableLiveData<Int> = MutableLiveData(),
    val fps: MutableLiveData<String> = MutableLiveData(),
    val showCapture: MutableLiveData<Boolean> = MutableLiveData(),
    val showRecord: MutableLiveData<Boolean> = MutableLiveData(),
    val previewEnabled: MutableLiveData<Boolean> = MutableLiveData(),
    val showCameraInfo: MutableLiveData<Boolean> = MutableLiveData(),
    val showRecording: MutableLiveData<Boolean> = MutableLiveData(),
    val recorded: MutableLiveData<String> = MutableLiveData(),
    val showMode: MutableLiveData<Boolean> = MutableLiveData(),
    val showRecordingPoint: MutableLiveData<Boolean> = MutableLiveData(),
    val recordSrc: MutableLiveData<Int> = MutableLiveData(),
    val isCapture: MutableLiveData<Boolean> = MutableLiveData(),
    val isRecord: MutableLiveData<Boolean> = MutableLiveData(),
    val isAutoFocus: MutableLiveData<Boolean> = MutableLiveData(),
    val showZoom: MutableLiveData<Boolean> = MutableLiveData(),
    val cameraInfo: MutableLiveData<String> = MutableLiveData(),
    val previewSrc: MutableLiveData<Int> = MutableLiveData(),
    val zoom: MutableLiveData<Int> = MutableLiveData(),
    val ae: MutableLiveData<Int> = MutableLiveData(),
    val photo: MutableLiveData<String> = MutableLiveData(),
    val action: (CameraAction, BaseEvent?) -> Unit
) {

    init {//set default values
        previewSrc.postValue(R.string.start_preview)
        showMode.postValue(null)
        showCapture.postValue(null)
        showRecord.postValue(null)
        showRecording.postValue(null)
        showRecordingPoint.postValue(null)
        showCameraInfo.postValue(null)
        previewEnabled.postValue(false)
        recordSrc.postValue(R.drawable.movie_record)
        isAutoFocus.postValue(true)

        showZoom.postValue(null)
        //checkout what display on screen

        showMode.observeForever {
            if (it == true) {
                if (isCapture.value == true) {
                    showCapture.postValue(true)
                    showRecord.postValue(null)
                    showRecording.postValue(null)
                    showRecordingPoint.postValue(null)
                } else if (isRecord.value == true) {
                    showCapture.postValue(null)
                    showRecord.postValue(true)
                    showRecording.postValue(null)
                    showRecordingPoint.postValue(null)
                }
            } else {
                showCapture.postValue(null)
                showRecordingPoint.postValue(null)
                showRecording.postValue(null)
                showRecord.postValue(null)
            }
        }
        //make sure the display when is in Capture mode
        isCapture.observeForever {
            if (it == true) {
                showCapture.postValue(true)
                showRecord.postValue(null)
                showRecording.postValue(null)
                showRecordingPoint.postValue(null)
            }
        }
        //make sure the display when is in record mode
        isRecord.observeForever {
            if (it == true) {
                showCapture.postValue(null)
                showRecord.postValue(true)
                showRecordingPoint.postValue(null)
                showRecordingPoint.postValue(null)
            }
        }

        //set camera focus mode by switcher.
        isAutoFocus.observeForever {
            RKGlassCamera.getInstance().setAutoFocus(it == true)
        }
        //set camera exposure by seekbar
        ae.observeForever {
            it?.let {
                RKGlassCamera.getInstance().exposure = if (it > 0) it else 0
            }
        }
        //set camera zoom by seekbar
        zoom.observeForever {
            if (it != null && it in 0..100) {
                RKGlassCamera.getInstance().zoom = it
            }
        }

    }

    fun onPreviewClicked(v: View) {
        when (previewSrc.value) {
            R.string.start_preview -> {//to start preview
                action(CameraAction.StartPreview, null)
                previewSrc.postValue(R.string.stop_preview)
                showMode.postValue(true)
                if (isCapture.value == true) showCapture.postValue(true) else showCapture.postValue(
                    null
                )
                if (isRecord.value == true) showRecord.postValue(true) else showRecord.postValue(
                    null
                )
                showZoom.postValue(RKApplication.INSTANCE.deviceType != DeviceType.AirProPlus)
            }
            else -> {//to stop preview
                action(CameraAction.StopPreview, null)
                previewSrc.postValue(R.string.start_preview)
                showCapture.postValue(null)
                showRecord.postValue(null)
                showMode.postValue(null)
                showZoom.postValue(null)
            }
        }
    }

    /**
     * On capture
     * capture pictures and store it in /sdcard/rkGlassText/ folder
     */
    private lateinit var dialog: Dialog
    private lateinit var showDialog: BasePopupView
    fun onCapture(v: View) {
        val savePath = File(v.context.filesDir, "${System.currentTimeMillis()}.jpg").absolutePath

        RKGlassCamera.getInstance()
            .takePicture(savePath) {
//                RKLogger.e("当前路径$it")
                action(CameraAction.Captured, object : BaseEvent() {
                    override fun doEvent(context: Context) {
//                        Toast.makeText(context, "saved to : $savePath", Toast.LENGTH_SHORT).show()
                        val bingding =
                            ImgFullBinding.inflate(LayoutInflater.from(context), null, false)
                        bingding.lifecycleOwner = context as AppCompatActivity
                        val img = bingding.img
                        Glide.with(context).load(it).into(img)
                        bingding.register.setOnClickListener {
                            if (bingding.etContent.text.isNotEmpty()) {
                                showDialog = XPopup.Builder(context)
                                    .dismissOnBackPressed(false)
                                    .dismissOnTouchOutside(false)
                                    .asLoading("注册中...")
                                    .show()
                                upLoadImage(savePath, bingding.etContent.text.toString())
                                //搜索按键action
                                MethodInputUtil.hideSoftInput(context, bingding.etContent)
                            } else {
                                Toast.makeText(context, "请输入名称", Toast.LENGTH_SHORT).show()
                            }
                        }
                        val alterDialogBuilder = AlertDialog.Builder(context)
                            .setCancelable(true)
                            .setPositiveButton(R.string.close) { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }
                            .setView(bingding.root)
//                        alterDialogBuilder.create().show()
                        dialog = alterDialogBuilder.show()

                    }
                })
            }
    }

    private fun upLoadImage(path: String, name: String) {
        val file = File(path)
        val createFormData = MultipartBody.Part.createFormData(
            "file",
            file.name,
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        )
        val name = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
        RetrofitNet.getInstance().api.faceRegister(name, createFormData)
            .enqueue(object : ResultCallback<Result<String>>() {
                override fun onSuccess(response: Response<Result<String>?>) {
                    photo.postValue("注册成功")
                    if (showDialog != null && showDialog.isShow()) {
                        showDialog.dismiss()
                        dialog.dismiss()
                    }

                }

                override fun onFail(message: String) {
                    if (showDialog != null && showDialog.isShow()) {
                        showDialog.dismiss()
                    }
                    photo.postValue(message)
                }
            })
    }

    private var threadOn = false
    private var recordShowThread: Thread? = null


    /**
     * On record
     * record or finish record camera data as a mp4 data, and store these in /sdcard/rkGlassTest/video folder
     */
    fun onRecord(v: View) {
        when (recordSrc.value) {
            R.drawable.movie_record -> {//start record
                recordSrc.postValue(R.drawable.play_pause)
                threadOn = true
                showRecording.postValue(true)
                recorded.postValue("0s")
                val startRecord = System.currentTimeMillis()
                recordShowThread = object : Thread() {
                    var count = false
                    override fun run() {
                        super.run()
                        while (threadOn) {
                            count = !count
                            showRecordingPoint.postValue(count)
                            val currentRecord = System.currentTimeMillis()
                            recorded.postValue("${((currentRecord - startRecord) / 1000) / 60}'${((currentRecord - startRecord) / 1000) % 60}\"")
                            sleep(500)
                        }
                    }
                }
                recordShowThread?.start()


                RKGlassCamera.getInstance().startRecord(RecordParams().apply {
                    isVoiceClose =
                        false//true to close voice record,just record video; False to enable record video with voice.
//            recordDuration = 6//set record duration to stop record automatic. Unit of minutes.
                    this.recordPath = File(v.context.filesDir, "video.mp4").absolutePath
                }, object : AbstractUVCCameraHandler.OnEncodeResultListener {
                    override fun onEncodeResult(
                        p0: ByteArray?,
                        p1: Int,
                        p2: Int,
                        p3: Long,
                        p4: Int
                    ) {
                        //you can add watermarker here or do something for camera raw data
                    }

                    override fun onRecordResult(path: String?) {//when finish record show a dialog to display the recorded video.
                        RKLogger.e("视频存储位置：$path")
                        action(CameraAction.Recorded, object : BaseEvent() {
                            override fun doEvent(context: Context) {
                                path ?: run { return } //if path is null, just return. Do noting
                                val dataBinding = VideoplayerBinding.inflate(
                                    LayoutInflater.from(context),
                                    null,
                                    false
                                )
                                dataBinding.lifecycleOwner = context as AppCompatActivity
                                val mediaData = MediaPlayerData()
                                dataBinding.data = mediaData
                                dataBinding.surface.surfaceTextureListener =
                                    object : TextureView.SurfaceTextureListener {
                                        override fun onSurfaceTextureAvailable(
                                            p0: SurfaceTexture,
                                            p1: Int,
                                            p2: Int
                                        ) {//init media data after surface available
                                            mediaData.init(path, Surface(p0))
                                        }

                                        override fun onSurfaceTextureSizeChanged(
                                            p0: SurfaceTexture,
                                            p1: Int,
                                            p2: Int
                                        ) {//do nothing
                                        }

                                        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                                            //stop media player
                                            mediaData.deInit()
                                            return true
                                        }

                                        override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {//do nothing

                                        }

                                    }
                                dataBinding.seek.setOnSeekBarChangeListener(object :
                                    SeekBar.OnSeekBarChangeListener {
                                    override fun onProgressChanged(
                                        p0: SeekBar?,
                                        p1: Int,
                                        p2: Boolean
                                    ) {
                                        if (p2) {//if is from user do follow
                                            mediaData.seekTo(p1)
                                        }
                                    }

                                    override fun onStartTrackingTouch(p0: SeekBar?) {
                                    }

                                    override fun onStopTrackingTouch(p0: SeekBar?) {
                                    }

                                })
                                //show play dialog for the last recorded video.
                                val alterDialogBuilder = AlertDialog.Builder(context)
                                    .setCancelable(false)
                                    .setView(dataBinding.root)
                                    .setPositiveButton(R.string.close) { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                    }
                                    .setOnDismissListener {//stop media player every dismiss
                                        mediaData.deInit()
                                    }
                                alterDialogBuilder.create().show()
                            }
                        })
                    }

                })
            }
            else -> {//to stop record
                threadOn = false
                showRecording.postValue(null)
                showRecordingPoint.postValue(null)
                recordShowThread = null
                RKGlassCamera.getInstance().stopRecord()
                recordSrc.postValue(R.drawable.movie_record)
            }
        }

    }

    fun clearData() {
        threadOn = false
        recordShowThread = null
        try {
            if (RKGlassCamera.getInstance()?.isRecording == true) {
                RKGlassCamera.getInstance()?.stopRecord()
            }
            RKGlassCamera.getInstance()?.stopPreview()
            if (RKGlassCamera.getInstance()?.isCameraOpened == true) {
                RKGlassCamera.getInstance()?.closeCamera()
            }
            RKGlassCamera.getInstance().deInit()
        } catch (e: Exception) {
        }
    }

}

/**
 * Media player data
 * Data model for showing Player
 * @property playedString time played
 * @property durationString duration of video
 * @property position seekbar position
 * @property playStatusSrc image to show on button
 * @property enablePlay is enable to start play or not
 */
data class MediaPlayerData(
    val playedString: MutableLiveData<String> = MutableLiveData(),
    val durationString: MutableLiveData<String> = MutableLiveData(),
    val position: MutableLiveData<Int> = MutableLiveData(),
    val playStatusSrc: MutableLiveData<Int> = MutableLiveData(),
    val enablePlay: MutableLiveData<Boolean> = MutableLiveData(),
) {

    private var totalDuration = 0
        set(value) {
            field = value
            durationString.postValue("${(value / 1000) / 3600}:${((value / 1000) / 60) % 60}:${(value / 1000) % 60}")
        }
    private var played: Int = 0
        set(value) {
            field = value
            if (totalDuration != 0) {
                position.postValue(value * 100 / totalDuration)
            }
        }

    private var mediaPlayer = MediaPlayer().apply {
        this.setOnPreparedListener {
            enablePlay.postValue(true)
            totalDuration = it.duration
        }
        this.setOnCompletionListener {
            playStatusSrc.postValue(R.drawable.play_start)
        }
    }

    private var threadOn = false
    private var playedThread: Thread? = null

    /**
     * Start time thread
     * Start to get played time.
     */
    private fun startTimeThread() {
        threadOn = true
        playedThread ?: run {
            playedThread = object : Thread() {
                override fun run() {
                    super.run()
                    while (threadOn) {
                        played = mediaPlayer.currentPosition
                        sleep(200)
                    }
                    playedThread = null
                }
            }
            playedThread?.start()
        }
    }

    fun init(path: String, surface: Surface) {
        enablePlay.postValue(false)
        mediaPlayer.setDataSource(path)
        mediaPlayer.prepare()
        mediaPlayer.setSurface(surface)
        playStatusSrc.postValue(R.drawable.play_start)
    }


    fun deInit() {
        threadOn = false
        mediaPlayer.release()
    }

    fun onPlay(v: View) {
        when (playStatusSrc.value) {
            R.drawable.play_start -> {
                mediaPlayer.start()
                playStatusSrc.postValue(R.drawable.play_pause)
                startTimeThread()
            }
            R.drawable.play_stop -> {
                mediaPlayer.prepare()
                playStatusSrc.postValue(R.drawable.play_start)
            }
            R.drawable.play_pause -> {
                mediaPlayer.pause()
                playStatusSrc.postValue(R.drawable.play_start)
            }
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer.seekTo(position * totalDuration / 100)
    }

}

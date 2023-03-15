package com.rokid.rkglassdemokotlin.camera

import android.Manifest
import android.app.Presentation
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.ContactsContract.Contacts.Photo
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.rokid.rkglassdemokotlin.FaceRectView
import com.rokid.rkglassdemokotlin.R
import com.rokid.rkglassdemokotlin.app.DeviceType
import com.rokid.rkglassdemokotlin.base.BaseActivity
import com.rokid.rkglassdemokotlin.base.DataBinding
import com.rokid.rkglassdemokotlin.databinding.ActivityCameraBinding
import com.rokid.rkglassdemokotlin.databinding.PreviewSingleBinding
import com.rokid.rkglassdemokotlin.network.Result
import com.rokid.rkglassdemokotlin.network.ResultCallback
import com.rokid.rkglassdemokotlin.network.RetrofitNet
import com.rokid.rkglassdemokotlin.utils.BitmapRequestBody
import com.rokid.rkglassdemokotlin.utils.Utils
import com.rokid.utils.ToastUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File


/**
 * Camera activity
 * Activity created base on [BaseActivity] to test Camera usage of the glass.
 * Make sure that this test is just for [DeviceType.AirPro] & [DeviceType.Glass2] & [DeviceType.AirProPlus]
 *
 */
class CameraActivity : BaseActivity() {
    //viewDataBinding
    private lateinit var dataBinding: ActivityCameraBinding
    private lateinit var model: CameraModel

    //base on BaseViewModel
    private lateinit var viewModel: CameraViewModel
    private var presentation: Presentation? = null
    private var preview_faceView: com.rokid.rkglassdemokotlin.view.FaceRectView? = null
    private var preview_name: TextView? = null
    private var tv_tip: TextView? = null
    private var questNum = 0

    //人脸数据
    var mFacelList: MutableList<FaceModel> = ArrayList()

    var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
                    //失败再次请求 判断mFacelList是否包含
                    mFacelList?.forEachIndexed { _, faceModel ->
                        if (faceModel.faceId == msg.arg1 && faceModel.name.isEmpty()) {
                            faceModel.requestsNum = 1
                        }
                    }
                }
                1 -> {
                    //成功
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivityCameraBinding.inflate(layoutInflater)
        dataBinding.lifecycleOwner = this
        setContentView(dataBinding.root)
//
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        viewModel = getViewModel(CameraViewModel::class.java)
//        viewModel.initFaceSDK()
        //make sure this is used in every activity based on BaseActivity.
        initViewModel(viewModel)
        //set dataBinding's source data.
        dataBinding.data = viewModel.getModel().apply {
            topHeight.postValue(DataBinding.getStatusBarHeight(this@CameraActivity))
            photo.observeForever {
                showTip(it)
            }
        }
        //this test will use camera and microphone to record videos, so the follows permissions should be confirmed.
        requestPermissions(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 666
        )
        //use the OnSurfaceListener provided by viewModel
        //to display on glass with presentation, on phone the view is black.
        val faceView = findViewById<FaceRectView>(R.id.faceView)

        (getSystemService(Context.DISPLAY_SERVICE) as DisplayManager).let {
            presentation = object : Presentation(this, it.displays[it.displays.size.minus(1)]) {
                private lateinit var binding: PreviewSingleBinding
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    binding = PreviewSingleBinding.inflate(layoutInflater)
                    binding.lifecycleOwner = this@CameraActivity
                    setContentView(binding.root)
                    binding.texture.surfaceTextureListener = viewModel.getSurfaceListener()
                    preview_name = findViewById(R.id.tv_name)
                    tv_tip = findViewById(R.id.tv_tip)
                    preview_faceView = findViewById(R.id.Preview_faceView)
                }
            }
            presentation?.show()
        }

        viewModel.run {

            rectfaceList.observeForever { it ->
                if (it.size == mFacelList.size && Utils.IsEquest(it, mFacelList)) {
                    // 遍历时获取索引位置
                    it.forEachIndexed { index, mIt ->
                        if (mFacelList[index]?.name.isEmpty() && mFacelList[index]?.requestsNum == 1) {
                            mFacelList[index]?.requestsNum = 0
                            //如果前一个没有获取到，重新获取，判断是否请求完成
                            getImageName(mIt.bitmap, mIt.faceId, index)
                        } else {
                            mIt.name = mFacelList[index]?.name
                        }
                    }
                } else {
                    //子线程人脸识别
                    mFacelList.clear()
                    mFacelList.addAll(it)
                    handler.removeMessages(0)
                    it.forEachIndexed { index, it ->
                        getImageName(it.bitmap, it.faceId, index)
                    }
                }
                if (it.isNotEmpty()) {
                    preview_faceView?.drawFaceRect(it, preview_name?.text.toString())
                } else {
                    preview_faceView?.clearRect()
                    preview_name?.text = ""
                    tv_tip?.text = "未检测到人脸"
                    mFacelList.clear()
                }
            }

        }
    }

    fun showTip(msg:String){
        ToastUtils.makeText(this,msg)
    }
    private fun getImageName(bitmap: Bitmap, faceId: Int, index: Int) {
        val bitmapRequestBody = BitmapRequestBody(bitmap)
        val createFormData =
            MultipartBody.Part.createFormData("file", faceId.toString(), bitmapRequestBody)
        val create = RequestBody.create("text/plain".toMediaTypeOrNull(), faceId.toString())
        RetrofitNet.getInstance().api.updateSignStrname(create, createFormData)
            .enqueue(object : ResultCallback<Result<FaceBean>>() {
                override fun onSuccess(response: Response<Result<FaceBean>?>) {
                    val body = response.body()
                    questNum++
                    tv_tip?.text = "成功$questNum"
                    if (mFacelList.size >= index) {
                        mFacelList[index]?.name = body?.data?.name
                    }
                }

                override fun onFail(message: String) {
                    if (message.contains("请先注册")) {
                        tv_tip?.text = "失败$message"
                    } else {
                        tv_tip?.text = ""
                        handler.sendMessage(Message().apply {
                            what = 0
                            arg1 = faceId
                        })
                        questNum++
                    }
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            666 -> {//for this is a test so every permission is required by default.
                var all = true
                grantResults.forEach {
                    all = (it == PackageManager.PERMISSION_GRANTED) && all
                }
                if (all) {
                    viewModel.startCamera()
                } else {//goto the application settings to request all permissions.
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.fromParts("package", packageName, null)
                    })
                    this.finish()
                }
            }
        }
    }

    override fun onDestroy() {
        viewModel.clearModel()
        //if use presentation to show, close it after use
        presentation?.dismiss()
        super.onDestroy()
    }

}
package com.rokid.rkglassdemokotlin.camera

import android.Manifest
import android.app.Presentation
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.rokid.rkglassdemokotlin.FaceRectView
import com.rokid.rkglassdemokotlin.R
import com.rokid.rkglassdemokotlin.app.DeviceType
import com.rokid.rkglassdemokotlin.base.BaseActivity
import com.rokid.rkglassdemokotlin.base.DataBinding
import com.rokid.rkglassdemokotlin.databinding.ActivityCameraBinding
import com.rokid.rkglassdemokotlin.databinding.PreviewSingleBinding
import com.rokid.rkglassdemokotlin.network.BitmapUtil
import com.rokid.rkglassdemokotlin.network.MatFactBitmap
import com.rokid.rkglassdemokotlin.network.Result
import com.rokid.rkglassdemokotlin.network.ResultCallback
import com.rokid.rkglassdemokotlin.network.RetrofitNet
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.io.IOException

/**
 * Camera activity
 * Activity created base on [BaseActivity] to test Camera usage of the glass.
 * Make sure that this test is just for [DeviceType.AirPro] & [DeviceType.Glass2] & [DeviceType.AirProPlus]
 *
 */
class CameraActivity : BaseActivity() {
    //viewDataBinding
    private lateinit var dataBinding: ActivityCameraBinding

    //base on BaseViewModel
    private lateinit var viewModel: CameraViewModel
    private var presentation: Presentation? = null
    private var preview_faceView: com.rokid.rkglassdemokotlin.view.FaceRectView? = null
    private var preview_name: TextView? = null
    private var tv_tip: TextView? = null

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
            rectfaceList.observeForever {
                if (it.isNotEmpty()) {
                    findViewById<ImageView>(R.id.image).setImageBitmap(it[0].bitmap)
                    preview_faceView?.drawFaceRect(it, preview_name?.text.toString())
                    tv_tip?.text = "检测到人脸数量==${it.size}  可信度---$it"
                } else {
                    preview_faceView?.clearRect()
                    tv_tip?.text = "未检测到人脸"
                    preview_name?.text = ""
                }
            }
        }
    }

    private fun upLoadImage(path: String) {
        val file = File(path)
        Log.e("TAG", "uploadHeadImage: File=====" + file.absolutePath)
        val create = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val createFormData = MultipartBody.Part.createFormData("file", file.name, create)
        RetrofitNet.getInstance().api.updateSignStr(createFormData)
            .enqueue(object : ResultCallback<Result<FaceBean>>() {
                override fun onSuccess(response: Response<Result<FaceBean>?>) {
                    val body = response.body()
                    preview_name?.text = body?.data?.key
                }

                override fun onFail(message: String) {
                    Log.e("HTTP::onFail", message)
                    preview_name?.text = "onFail==$message"
                }
            })
    }

    fun base64ToBitmap(base64Data: String?): Bitmap? {
        val bytes = Base64.decode(base64Data, Base64.NO_WRAP)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
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
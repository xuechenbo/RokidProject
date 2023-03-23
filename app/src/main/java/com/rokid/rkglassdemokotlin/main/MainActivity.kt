package com.rokid.rkglassdemokotlin.main

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.FaceEngine
import com.arcsoft.face.enums.DetectFaceOrientPriority
import com.arcsoft.face.enums.DetectMode
import com.rokid.rkglassdemokotlin.R
import com.rokid.rkglassdemokotlin.app.RKApplication
import com.rokid.rkglassdemokotlin.base.BaseActivity
import com.rokid.rkglassdemokotlin.base.DataBinding
import com.rokid.rkglassdemokotlin.camera.ShowResumeMsgDialog
import com.rokid.rkglassdemokotlin.databinding.ActivityMainBinding
import com.rokid.utils.ContextUtil
import java.io.ByteArrayOutputStream


/**
 * Main activity
 * This activity is build if first start activity or connection error occurred.
 * @constructor Create empty Main activity
 */
class MainActivity : BaseActivity() {

    private lateinit var dataBinding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    val faceEngine = FaceEngine()

    var lastBackPressed: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivityMainBinding.inflate(layoutInflater)
        dataBinding.lifecycleOwner = this
        setContentView(dataBinding.root)
        viewModel = getViewModel(MainViewModel::class.java)
        initViewModel(viewModel)
        dataBinding.data = viewModel.getModel(DataBinding.getStatusBarHeight(this))
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //初始化虹软人脸
        initFaceEngine()
//        val drawableToBmp = BitmapUtil.drawableToBmp(this, R.drawable.ym);
//        getImageName(drawableToBmp, 1)

//        var showResumeMsgDialog = ShowResumeMsgDialog.getInstance("string")
//        showResumeMsgDialog.show(supportFragmentManager, "")

    }


    private fun initFaceEngine() {
        //debug包
//        val APP_ID = "7eQRFaiqEXoj5KysU5hT7Lidpdvzz9WxyKpfpa9mfyDH"
//        val SDK_KEY = "FnpHdxcZzaLPwc9NfRGaTvfmYCPc9azhWoWVc8EHEdCu"
//        val ACTIVE_KEY = "85Q1-11GJ-213Z-N732"
        //release包
//        val APP_ID = "7eQRFaiqEXoj5KysU5hT7Lidpdvzz9WxyKpfpa9mfyDH"
//        val SDK_KEY = "FnpHdxcZzaLPwc9NfRGaTvfmYCPc9azhWoWVc8EHEdCu"
//        val ACTIVE_KEY = "85Q1-11GJ-212H-4N4G"
        val APP_ID = "7eQRFaiqEXoj5KysU5hT7Lidpdvzz9WxyKpfpa9mfyDH"
        val SDK_KEY = "FnpHdxcZzaLPwc9NfRGaTvfmYCPc9azhWoWVc8EHEdCu"
        val ACTIVE_KEY = "85Q1-11GJ-211K-VCD8"
        val code = FaceEngine.activeOnline(this, ACTIVE_KEY, APP_ID, SDK_KEY)
        if (code == ErrorInfo.MOK) {
            Toast.makeText(applicationContext, "初始化成功", Toast.LENGTH_SHORT).show()
            Log.i("TAG", "activeOnline success")
        } else if (code == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
            Toast.makeText(applicationContext, "已经激活", Toast.LENGTH_SHORT).show()
            Log.i("TAG", "already activated")
        } else {
            Toast.makeText(applicationContext, "初始化失败" + code, Toast.LENGTH_SHORT).show()
            Log.i("TAG", "activeOnline failed, code is : " + code);
        }
    }

    fun initFaceSDK() {
        // 如下的组合，初始化的功能包含：人脸检测、人脸识别
        val initMask = FaceEngine.ASF_FACE_DETECT or FaceEngine.ASF_FACE_RECOGNITION
        val code = faceEngine.init(
            ContextUtil.getApplicationContext(),
            DetectMode.ASF_DETECT_MODE_VIDEO,
            DetectFaceOrientPriority.ASF_OP_0_ONLY,
            5,
            initMask
        )
        Log.e("TAG", "code=" + code)
    }


    private fun init() {
        val image = findViewById<ImageView>(R.id.image)
        val drawableToBmp = drawableToBmp(RKApplication.appContext, R.drawable.qqw)

        val bitmapToString = drawableToBmp?.let { bitmapToBase64(it) }

        val base64ToBitmap = base64ToBitmap(bitmapToString)
        image.setImageBitmap(base64ToBitmap)

//        val imageString = Sample.getImageString111(bitmapToString)
    }

    fun bitmapToBase64(bitmap: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return if (byteArrayOutputStream.toByteArray() == null) {
            null
        } else Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP)
    }


    fun base64ToBitmap(base64Data: String?): Bitmap? {
        val bytes = Base64.decode(base64Data, Base64.NO_WRAP)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }


    fun drawableToBmp(context: Context, drawableId: Int): Bitmap? {
        val d = context.resources.getDrawable(drawableId)
        return drawableToBmp(d)
    }

    fun drawableToBmp(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }


    /**
     * On back pressed
     * Double clicked to quite this application.
     */
    override fun onBackPressed() {
        if (System.currentTimeMillis() - lastBackPressed < 1000L) {
            finish()
        } else {
            Toast.makeText(this, getString(R.string.quite), Toast.LENGTH_SHORT).show()
        }
        lastBackPressed = System.currentTimeMillis()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            viewModel.cameraRequest -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.startConnect()
                }
            }
        }
    }
}
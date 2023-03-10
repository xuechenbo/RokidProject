package com.rokid.rkglassdemokotlin.hardware

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.rokid.rkglassdemokotlin.R
import com.rokid.rkglassdemokotlin.base.BaseActivity
import com.rokid.rkglassdemokotlin.base.DataBinding
import com.rokid.rkglassdemokotlin.databinding.ActivityHardwareBinding

/**
 * Hardware activity
 * Activity to show Glass hardware information such as IMU information、 usage of microphone、 brightness control、shown mode(in 2D or 3D), ect..
 *
 */
class HardwareActivity : BaseActivity() {

    private lateinit var dataBinding: ActivityHardwareBinding
    private lateinit var viewModel: HardwareViewModel

    private val phoneStatePermission = 1234


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivityHardwareBinding.inflate(layoutInflater)
        dataBinding.lifecycleOwner = this
        viewModel = getViewModel(HardwareViewModel::class.java)
        initViewModel(viewModel)
        setContentView(dataBinding.root)
        dataBinding.data = viewModel.getModel(DataBinding.getStatusBarHeight(this))
        //when start glass hardware testing, READ_PHONE_STATE is required.
        requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), phoneStatePermission)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            phoneStatePermission -> {
                var allGet = true
                grantResults.forEach {
                    allGet = allGet && (it == PackageManager.PERMISSION_GRANTED)
                }
                if (!allGet) {//out of permission, back
                    onBackPressed()
                }
            }
            viewModel.audioStatePermission -> {
                var allGet = true
                grantResults.forEach {
                    allGet = allGet && (it == PackageManager.PERMISSION_GRANTED)
                }
                if (!allGet) {
                    Toast.makeText(this, getString(R.string.please_check_permission), Toast.LENGTH_SHORT).show()

                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.fromParts("package", packageName, null)
                    })
                    this.finish()

                } else {//start record audio after permissions obtain.
                    viewModel.showRecord()
                }
            }
        }
    }

}
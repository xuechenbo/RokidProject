package com.rokid.rkglassdemokotlin.voice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.rokid.rkglassdemokotlin.R
import com.rokid.rkglassdemokotlin.base.BaseActivity
import com.rokid.rkglassdemokotlin.base.DataBinding
import com.rokid.rkglassdemokotlin.databinding.ActivityVoiceBinding

class VoiceActivity : BaseActivity() {

    private lateinit var dataBinding: ActivityVoiceBinding
    private lateinit var viewModel: VoiceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivityVoiceBinding.inflate(layoutInflater)
        dataBinding.lifecycleOwner = this
        viewModel = getViewModel(VoiceViewModel::class.java)
        initViewModel(viewModel)
        setContentView(dataBinding.root)
        dataBinding.data = viewModel.getModel(DataBinding.getStatusBarHeight(this))

        dataBinding.rcAdded.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        dataBinding.rcAdded.adapter = viewModel.getAdapter(this)

//        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), viewModel.permissionRequest)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), viewModel.permissionRequest)
//        viewModel.startDefault(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            viewModel.permissionRequest ->{
                var allget = true
                grantResults.forEach {
                    allget = ((it == PackageManager.PERMISSION_GRANTED) and allget)
                }
                if (allget){
                    viewModel.startDefault(this)
                }else{
                    Toast.makeText(this, getString(R.string.please_check_permission), Toast.LENGTH_SHORT).show()

                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.fromParts("package", packageName, null)
                    })
                    this.finish()
                }
            }
        }
    }

}
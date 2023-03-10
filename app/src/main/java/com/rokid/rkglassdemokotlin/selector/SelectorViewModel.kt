package com.rokid.rkglassdemokotlin.selector

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.rokid.logger.RKLogger
import com.rokid.rkglassdemokotlin.app.DeviceType
import com.rokid.rkglassdemokotlin.app.RKApplication
import com.rokid.rkglassdemokotlin.base.BaseEvent
import com.rokid.rkglassdemokotlin.base.BaseViewModel
import com.rokid.rkglassdemokotlin.camera.CameraActivity
import com.rokid.rkglassdemokotlin.hardware.HardwareActivity
import com.rokid.rkglassdemokotlin.presentation.PresentationActivity
import com.rokid.rkglassdemokotlin.voice.VoiceActivity

class SelectorViewModel: BaseViewModel() {

    private lateinit var model: SelectorModel

    fun getModel(statusBarHeight: Int): SelectorModel{
        model = SelectorModel{
            when(it){
                SelectorType.Back ->{//Back pressed
                    event.postValue(object : BaseEvent(){
                        override fun doEvent(context: Context) {
                            if (context is AppCompatActivity){
                                context.onBackPressed()
                            }
                        }
                    })
                }
                SelectorType.Hardware -> {//to start Hardware testing Activity.
                    event.postValue(object : BaseEvent(){
                        override fun doEvent(context: Context) {
                            context.startActivity(Intent(context, HardwareActivity::class.java))
                        }
                    })
                }
                SelectorType.Camera -> {//to start Camera testing Activity.
                    event.postValue(object : BaseEvent(){
                        override fun doEvent(context: Context) {
                            context.startActivity(Intent(context, CameraActivity::class.java))
                        }
                    })
                }
                SelectorType.Voice -> {//to start Voice recognize testing Activity.
                    event.postValue(object : BaseEvent(){
                        override fun doEvent(context: Context) {
                            context.startActivity(Intent(context, VoiceActivity::class.java))
                        }
                    })
                }
                SelectorType.Presentation -> {//to start Presentation testing Activity.
                    event.postValue(object : BaseEvent(){
                        override fun doEvent(context: Context) {
                            context.startActivity(Intent(context, PresentationActivity::class.java))
                        }
                    })
                }
            }
        }.apply {
            topHeight.postValue(statusBarHeight)
            //For Rokid Air is not include a camera, so dismiss the button.
            showCamera.postValue(when(RKApplication.INSTANCE.deviceType){
                DeviceType.Glass2, DeviceType.AirPro, DeviceType.AirProPlus -> true
                else -> null
            })
        }
        return model
    }
}
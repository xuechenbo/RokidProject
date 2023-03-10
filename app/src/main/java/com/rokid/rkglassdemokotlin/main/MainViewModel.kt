package com.rokid.rkglassdemokotlin.main

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rokid.rkglassdemokotlin.base.BaseEvent
import com.rokid.rkglassdemokotlin.base.BaseViewModel

class MainViewModel: BaseViewModel() {
    private lateinit var model: MainModel
    val cameraRequest = 4123
    fun getModel(statusBarHeight: Int): MainModel{
        model = MainModel {
            when(it){
                MainActionType.Back -> {//Motion Back is clicked.
                    event.postValue(object : BaseEvent(){
                        override fun doEvent(context: Context) {
                            if (context is AppCompatActivity){
                                context.onBackPressed()
                            }
                        }
                    })
                }
                MainActionType.NeedPermission -> {//when using Pro + "Camera Permission" is required.
                    event.postValue(object : BaseEvent(){
                        override fun doEvent(context: Context) {
                            if (context is AppCompatActivity){
                                //Air Pro + Glass Need Camera Permission when connect.
                                context.requestPermissions(arrayOf(Manifest.permission.CAMERA), cameraRequest)
                            }
                        }
                    })
                }
            }
        }.apply {
            topHeight.postValue(statusBarHeight)
        }
        return model
    }
    //After checkout which type device is used, start to connect to glass.
    fun startConnect() {
        model.onNeedConnect()
    }

}
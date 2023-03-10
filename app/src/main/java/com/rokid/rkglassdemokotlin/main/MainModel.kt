package com.rokid.rkglassdemokotlin.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.rokid.axr.phone.glassdevice.RKGlassDevice
import com.rokid.rkglassdemokotlin.test.Sample
import com.rokid.rkglassdemokotlin.app.RKApplication

enum class MainActionType {
    Back, NeedPermission
}

class MainModel(
    val topHeight: MutableLiveData<Int> = MutableLiveData(),
    val isAirProPlus: MutableLiveData<Boolean> = MutableLiveData(),
    val action: (MainActionType) -> Unit
) {
    init {
        isAirProPlus.postValue(false)
        isAirProPlus.observeForever {
            if (it == true) {
                //Air Pro + Glass Need Camera Permission when connect.
                action(MainActionType.NeedPermission)
            }else{//
                onNeedConnect()
            }
        }
    }

    fun onBackPressed(v: View) {
        action(MainActionType.Back)
    }

    /**
     * On need connect
     * Do device connect using init function.
     */
    fun onNeedConnect() {
        RKGlassDevice.getInstance().init(RKApplication.INSTANCE.connectListener)
    }

    fun upImage(v: View){
        Sample.getImageString("")
    }
}
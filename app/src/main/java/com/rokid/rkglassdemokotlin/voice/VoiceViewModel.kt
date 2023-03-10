package com.rokid.rkglassdemokotlin.voice

import androidx.appcompat.app.AppCompatActivity
import com.rokid.axr.phone.glassvoice.RKOfflineCommandManager
import com.rokid.rkglassdemokotlin.base.BaseViewModel

class VoiceViewModel : BaseViewModel() {

    val permissionRequest: Int = 1238
    private lateinit var model: VoiceModel
    var offlineManager: RKOfflineCommandManager? = null

    fun getModel(topHeight: Int): VoiceModel {
        model = VoiceModel {
            event.postValue(it)
        }.apply {
            this.topHeight.postValue(topHeight)
        }
        return model
    }

    fun getAdapter(context: AppCompatActivity): AddedAdapter {
        return model.getRcAdapter(context)
    }

    fun startDefault(context: AppCompatActivity) {



        offlineManager = RKOfflineCommandManager().apply {
            this.bindLifecycle(context)
        }
        model.initOffline(offlineManager!!)
    }

    fun destroy() {
        offlineManager = null
    }
}
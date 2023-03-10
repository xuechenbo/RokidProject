package com.rokid.rkglassdemokotlin.selector

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.rokid.logger.RKLogger

/**
 * Selector type
 *
 */
enum class SelectorType {
    Hardware, Camera, Voice, Presentation, Back
}

data class SelectorModel(
    val topHeight: MutableLiveData<Int> = MutableLiveData(),
    val showCamera: MutableLiveData<Boolean> = MutableLiveData(),
    val action: (SelectorType) -> Unit
) {

    fun onBack(v: View){
        action(SelectorType.Back)
    }

    fun onHardwareTest(v: View) {
        action(SelectorType.Hardware)
    }

    fun onCameraDemo(v: View) {
        action(SelectorType.Camera)
    }

    fun onVoiceDemo(v: View) {
        action(SelectorType.Voice)
    }

    fun onPresentation(v: View) {
        action(SelectorType.Presentation)
    }
}
package com.rokid.rkglassdemokotlin.presentation

import android.app.Presentation
import android.content.Context
import android.content.Context.DISPLAY_SERVICE
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.rokid.rkglassdemokotlin.R
import com.rokid.rkglassdemokotlin.base.BaseEvent
import com.rokid.rkglassdemokotlin.databinding.PresentationHBinding


class PresentationModel(
    val topHeight: MutableLiveData<Int> = MutableLiveData(),
    val enableShow: MutableLiveData<Boolean> = MutableLiveData(),
    val numberString: MutableLiveData<String> = MutableLiveData(),
    val action: (BaseEvent) -> Unit
) {
    private var displayManager: DisplayManager? = null
    private var showingPresentation: Presentation? = null

    var number = 0
        set(value) {
            field = if (value in 0..Int.MAX_VALUE) value else 0
            numberString.postValue("$field")
        }

    init {
        enableShow.postValue(true)
    }

    fun onBackPressed(v: View){
        action(object : BaseEvent(){
            override fun doEvent(context: Context) {
                if (context !is AppCompatActivity) return
                context.onBackPressed()
            }
        })
    }

    fun onPlus(v: View) {
        number++
    }

    fun onMoin(v: View) {
        number--
    }

    fun onShowPresentation(v: View) {
        val event = object : BaseEvent() {
            override fun doEvent(context: Context) {
                super.doEvent(context)
                if (context !is AppCompatActivity) return
                displayManager = context.getSystemService(DISPLAY_SERVICE) as DisplayManager
                displayManager?.let {
                    if (it.displays.size > 1) {
                        showPresentation(context, it.displays[it.displays.size.minus(1)])
                    }

                }
            }
        }
        action(event)
        enableShow.postValue(false)
    }

    fun onDismissPresentation(v: View) {
        displayManager = null
        showingPresentation?.dismiss()
        enableShow.postValue(true)
    }

    private fun showPresentation(activity: AppCompatActivity, display: Display) {
        showingPresentation = object : Presentation(activity, display, R.style.GlassTheme) {
            private lateinit var dataBinding: PresentationHBinding
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                dataBinding =
                    PresentationHBinding.inflate(LayoutInflater.from(context), null, false)
                dataBinding.lifecycleOwner = activity
                dataBinding.data = this@PresentationModel
                setContentView(dataBinding.root)
            }
        }
        showingPresentation?.show()
    }

}
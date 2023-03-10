package com.rokid.rkglassdemokotlin.hardware

import android.Manifest
import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.rokid.axr.phone.glassdevice.RKGlassDevice
import com.rokid.axr.phone.glassdevice.hw.listener.RKKeyListener
import com.rokid.rkglassdemokotlin.R
import com.rokid.rkglassdemokotlin.base.BaseEvent
import com.rokid.rkglassdemokotlin.base.BaseViewModel
import com.rokid.rkglassdemokotlin.databinding.DialogRecordPlayAudioBinding
import java.io.File

class HardwareViewModel : BaseViewModel() {

    private lateinit var model: HardwareModel
//    val file: File by lazy {
//        File(Environment.getE(Environment.DIRECTORY_MUSIC).absolutePath + "/rokid_test.pcm")
//    }

    private lateinit var file: File

    val audioStatePermission = 1235

    fun getModel(statusBarHeight: Int): HardwareModel {
        model = HardwareModel {
            when (it) {
                HardwareEvent.Back -> {//back pressed
                    event.postValue(object : BaseEvent() {
                        override fun doEvent(context: Context) {
                            if (context is AppCompatActivity) {
                                context.onBackPressed()
                            }
                        }
                    })
                }
                HardwareEvent.Mic -> {//microphone testing clicked
                    event.postValue(object : BaseEvent() {
                        override fun doEvent(context: Context) {
                            if (context is AppCompatActivity) {
                                context.requestPermissions(
                                    arrayOf(
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ), audioStatePermission
                                )
                            }
                        }
                    })
                }
            }
        }.apply {
            topHeight.postValue(statusBarHeight)
        }
        /**
         * Add key event listener
         */
        RKGlassDevice.getInstance()?.setRkKeyListener(object : RKKeyListener {
            override fun onPowerKeyEvent(p0: Int) {
                event.postValue(object : BaseEvent() {
                    override fun doEvent(context: Context) {
                        Toast.makeText(context, "Power key is Clicked", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onBackKeyEvent(p0: Int) {//Only Glass2 has this
                event.postValue(object : BaseEvent() {
                    override fun doEvent(context: Context) {
                        Toast.makeText(context, "Back key is Clicked", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onTouchKeyEvent(p0: Int) {//Only Glass2 has this
                event.postValue(object : BaseEvent() {
                    override fun doEvent(context: Context) {
                        Toast.makeText(context, "Touched Key Event action", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }

            override fun onTouchSlideBack() {//Only Glass2 has this
                event.postValue(object : BaseEvent() {
                    override fun doEvent(context: Context) {
                        Toast.makeText(context, "Slide Back is actioned", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onTouchSlideForward() {//Only Glass2 has this
                event.postValue(object : BaseEvent() {
                    override fun doEvent(context: Context) {
                        Toast.makeText(context, "Slide Forward key is actioned", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }

        })

        return model
    }

    /**
     * Show record
     * Show a dialog to start Audio Record or play the last recorded audio.
     */
    fun showRecord() {
        event.postValue(object : BaseEvent() {


            override fun doEvent(context: Context) {
                super.doEvent(context)


                lateinit var dialog: AlertDialog
                //audioRecord Model
                file = File(context.filesDir, "rokid_test.pcm")
                val audioModel: AudioRecordModel = AudioRecordModel(file) {
                    when (it) {
                        -1-> {//exit
                            dialog.dismiss()
                        }
                    }
                }.apply {
                    if (audioRecordFile.exists()) {
                        this.showPlayLast.postValue(true)
                        this.canPlay.postValue(true)
                    } else {
                        this.showPlayLast.postValue(null)
                        this.canPlay.postValue(false)
                    }
                    recordSrc.postValue(R.string.start_record)
                    showRecording.postValue(true)
                    showRecordBtn.postValue(true)
                }

                if (context is AppCompatActivity) {
                    val dialogBuilder = AlertDialog.Builder(context)
                    val viewBinding = DialogRecordPlayAudioBinding.inflate(
                        LayoutInflater.from(context),
                        null,
                        false
                    )
                    viewBinding.lifecycleOwner = context
                    dialog = dialogBuilder.setView(viewBinding.root)
                        .setCancelable(false)
                        .create()
                    viewBinding.data = audioModel
                    dialog.show()
                }
            }
        })
    }

}
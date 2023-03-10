package com.rokid.rkglassdemokotlin.app

import android.app.Application
import android.content.Context
import android.hardware.usb.UsbDevice
import androidx.lifecycle.MutableLiveData
import com.rokid.axr.phone.glassdevice.RKGlassDevice
import com.rokid.axr.phone.glassdevice.callback.OnGlassDeviceConnectListener
import com.rokid.axr.phone.glassdevice.callback.OnUSBDevicePermissionListener
import com.rokid.logger.Logger

/**
 * R k application
 * This application is a demo of glass usage
 * @constructor Create empty R k application
 */
class RKApplication : Application() {

    companion object {
        lateinit var INSTANCE: RKApplication
        lateinit var appContext: Context
    }

    var deviceType: DeviceType? = null


    //checkout deviceType when device is changed
    var device: UsbDevice? = null
        set(value) {
            field = value
            val name = value?.productName
            deviceType = if (name?.contains("air", true) == true) {//Air Glass
                if (name.contains("pro", true)) {//Air Pro Glass
                    if (name.contains("+", true)) {//Air Pro + Glass
                        DeviceType.AirProPlus
                    } else {
                        DeviceType.AirPro
                    }
                } else {
                    DeviceType.Air
                }
            } else if (name?.contains("Kenobi", true) == true) {//Rokid Glass2 glass
                DeviceType.Glass2
            } else {
                null
            }
        }

    // For this application is build for testing RK Glasses.
    // So the connection status will be listened forever.
    val connectStatus: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * Connect listener
     * ROKID GLASS Devices connection listener
     * Be carefully of using Air Pro +, because Air Pro+ is made with imus and camera as one device, so camera permission is required before to connect to the device.
     * Add this listener to [RKGlassDevice], after things ready.
     */
    val connectListener: OnGlassDeviceConnectListener by lazy {
        object : OnGlassDeviceConnectListener {
            override fun onGlassDeviceConnected(p0: UsbDevice?) {//call when glass is connected
                device = p0
                Logger.e("usbDevice == ${p0?.productName}")
                connectStatus.postValue(true)
                var abc = "65536"
            }

            override fun onGlassDeviceDisconnected() {//call when glass is disconnected
                connectStatus.postValue(false)
                device = null
            }
        }
    }
    //

    /**
     * On create
     * init application
     */
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        appContext = applicationContext
    }
}

/**
 * Device type
 * Refer to SDK supported Rokid Glass Types
 */
enum class DeviceType {
    Glass2,//Monocular glass
    Air,//Binocular glass
    AirPro,//Binocular glass with a camera
    AirProPlus//Binocular glass with a camera sync timestamp with IMUS.
}
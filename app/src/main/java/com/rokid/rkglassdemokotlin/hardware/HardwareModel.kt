package com.rokid.rkglassdemokotlin.hardware

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.rokid.axr.phone.glassdevice.RKGlassDevice
import com.rokid.axr.phone.glassdevice.hw.GlassSensorListener
import com.rokid.rkglassdemokotlin.R
import com.rokid.rkglassdemokotlin.app.DeviceType
import com.rokid.rkglassdemokotlin.app.RKApplication

enum class HardwareEvent {
    Back, Mic
}

/**
 * Hardware model
 * Mode found for hardware testing
 * @property topHeight
 * @property getInfo source of string, that shown on button get Information
 * @property showGlassInfo show or dismiss glass information.
 * @property info device hardware information.
 * @property brightnessStr brightness value [String]
 * @property brightness brightness value [Int]
 * @property is2D radio button that make glass shown in 2D mode
 * @property sensorValue sensors date
 * @property is3D radio button that make galss shown in 3D mode
 * @property showPSensor Air/Air Pro/Air Pro + has P-Sensor
 * @property pSensorString
 * @property showLSensor Glass2 has L-Sensor
 * @property lSensorString
 * @property showSameSensor other Sensors
 * @property rotationVector Rotation Vector
 * @property gameRotationVector Game Rotation Vector
 * @property accelerateValues
 * @property magnetValues
 * @property gyroscopeValues
 * @property showAcc glass2 isn't have accelerate sensor
 * @property showGyroscope glass2 isn't have gyroscope sensor
 * @property action
 */
class HardwareModel(
    val topHeight: MutableLiveData<Int> = MutableLiveData(),
    val getInfo: MutableLiveData<Int> = MutableLiveData(),
    val showGlassInfo: MutableLiveData<Boolean> = MutableLiveData(),
    val info: MutableLiveData<String> = MutableLiveData(),
    val brightnessStr: MutableLiveData<String> = MutableLiveData(),
    val brightness: MutableLiveData<Int> = MutableLiveData(),
    val is2D: MutableLiveData<Boolean> = MutableLiveData(),
    val sensorValue: MutableLiveData<Int> = MutableLiveData(),
    val is3D: MutableLiveData<Boolean> = MutableLiveData(),
    val showPSensor: MutableLiveData<Boolean> = MutableLiveData(),
    val pSensorString: MutableLiveData<String> = MutableLiveData(),
    val showLSensor: MutableLiveData<Boolean> = MutableLiveData(),
    val lSensorString: MutableLiveData<String> = MutableLiveData(),
    val showSameSensor: MutableLiveData<Boolean> = MutableLiveData(),
    val rotationVector: MutableLiveData<String> = MutableLiveData(),
    val gameRotationVector: MutableLiveData<String> = MutableLiveData(),
    val accelerateValues: MutableLiveData<String> = MutableLiveData(),
    val magnetValues: MutableLiveData<String> = MutableLiveData(),
    val gyroscopeValues: MutableLiveData<String> = MutableLiveData(),
    val showAcc: MutableLiveData<Boolean> = MutableLiveData(),
    val showGyroscope: MutableLiveData<Boolean> = MutableLiveData(),
    val action: (HardwareEvent) -> Unit

) {

    init {
        getInfo.observeForever {
            if (it == R.string.show_glass_information){
                showGlassInfo.postValue(null)
                info.postValue(null)
            }else if (it == R.string.hide_glass_information){
                showGlassInfo.postValue(true)
                getHardwareInfo()
            }
        }
        getInfo.postValue(R.string.show_glass_information)

        RKGlassDevice.getInstance()?.brightness?.let {
            brightness.postValue(it)
        }?:run{
            brightness.postValue(100)
        }

        brightness.observeForever {bright ->
            bright?.let {
                RKGlassDevice.getInstance()?.brightness = it//set brightness
                brightnessStr.postValue("Brightness : ${RKGlassDevice.getInstance().brightness}")
            }
        }

        RKGlassDevice.getInstance()?.displayMode?.let {
            when(it){
                RKGlassDevice.GlassDisplayMode.MODE_2D -> {
                    is2D.postValue(true)
                }
                RKGlassDevice.GlassDisplayMode.MODE_3D -> {
                    is3D.postValue(true)
                }
                RKGlassDevice.GlassDisplayMode.Unknow -> {}
            }
        }
//        setDisplayMode
//        RKGlassDevice.getInstance().setDisplayMode(RKGlassDevice.GlassDisplayMode.XXXX)
        is2D.observeForever {aBoolean->
            if (aBoolean == true) {
                RKGlassDevice.getInstance().displayMode = RKGlassDevice.GlassDisplayMode.MODE_2D
            }
        }

        is3D.observeForever {aBoolean->
            if (aBoolean == true) {
                if (RKApplication.INSTANCE.deviceType == DeviceType.Glass2){//Glass2 have no mode called MODE_3D
                    is2D.postValue(true)
                }else {
                    RKGlassDevice.getInstance().displayMode = RKGlassDevice.GlassDisplayMode.MODE_3D
                }
            }
        }

        sensorValue.observeForever {//when show or dismiss sensors value.
            if (it == R.string.get_sensor_values){//dismiss
                RKGlassDevice.getInstance()?.setGlassSensorListener(null)
                showSameSensor.postValue(null)
                showPSensor.postValue(null)
                showLSensor.postValue(null)
                showGyroscope.postValue(null)
                showAcc.postValue(null)
            }else if (it == R.string.stop_get_sensor_values){//show
                //first init all as visible
                showPSensor.postValue(true)
                showLSensor.postValue(null)
                showAcc.postValue(true)
                showSameSensor.postValue(true)
                showGyroscope.postValue(true)
                //Which information shell be shown on screen, is decided by device type.
                when(RKApplication.INSTANCE.deviceType){
                    DeviceType.Glass2 ->{
                        showAcc.postValue(null)
                        showGyroscope.postValue(null)
                        showLSensor.postValue(true)
                        showPSensor.postValue(null)
                    }
                    DeviceType.AirProPlus->{
                        showPSensor.postValue(null)
                    }
                    else ->{}
                }
                RKGlassDevice.getInstance()?.setGlassSensorListener(object : GlassSensorListener{
                    override fun onPSensorUpdate(p0: Boolean) {
                        pSensorString.postValue("P-Sensor: $p0")
                    }

                    override fun onLSensorUpdate(p0: Int) {
                        lSensorString.postValue("L-Sensor: $p0")
                    }

                    override fun OnRotationVectorUpdate(p0: Long, p1: FloatArray?) {
                        rotationVector.postValue("Rotation Vector:\ntime stamp:$p0 ${p1?.showString()}")
                    }

                    override fun OnGameRotationVectorUpdate(p0: Long, p1: FloatArray?) {
                        gameRotationVector.postValue("Game Rotation Vector:\ntime stamp:$p0\n${p1?.showString()}")
                    }

                    override fun OnAcceleroMeterEvent(p0: Long, p1: FloatArray?) {
                        accelerateValues.postValue("Accelerometer Values::\ntime stamp:$p0\n${p1?.showString()}")
                    }

                    override fun OnMagnetFieldEvent(p0: Long, p1: FloatArray?) {
                        magnetValues.postValue("Magnet Values::\ntime stamp:$p0\n${p1?.showString()}")
                    }

                    override fun OnGyroscopEvnet(p0: Long, p1: FloatArray?) {
                        gyroscopeValues.postValue("Gyroscope Values::\ntime stamp:$p0\n${p1?.showString()}")
                    }

                    override fun OnVsyncTimeUpdate(p0: Long) {

                    }

                })
            }
        }
        sensorValue.postValue(R.string.get_sensor_values)
        pSensorString.postValue("P-Sensor: ")
        lSensorString.postValue("L-Sensor: ")


    }

    fun onBack(v: View) {
        action(HardwareEvent.Back)
    }

    fun getInfo(v: View) {//show or dismiss hardware information.
        if (getInfo.value == R.string.show_glass_information){
            getInfo.postValue(R.string.hide_glass_information)

        }else{
            getInfo.postValue(R.string.show_glass_information)
        }
    }

    /**
     * Get hardware info
     * Get Glass device information when device[UsbDevice] is NOT null
     * And get Rokid glass hardware information when Rokid glass device [RKGlassDevice] is NOT null.
     */
    private fun getHardwareInfo(){
        RKApplication.INSTANCE.device?.let {//get device's information with USB information And special Rokid Glass Information.
            val str = """
                        DeviceName = ${it.deviceName}
                        ProductName = ${it.productName}
                        SN = ${it.serialNumber}
                        ManufacturerName = ${it.manufacturerName}
                        ProductId = ${it.productId}
                        VendorId = ${it.vendorId}
                        Version = ${it.version}
                        RK_pcba = ${RKGlassDevice.getInstance()?.glassInfo?.pcba}
                        RK_sn = ${RKGlassDevice.getInstance()?.glassInfo?.sn}
                        RK_opticalId = ${RKGlassDevice.getInstance()?.glassInfo?.opticalId}
                        RK_typeId = ${RKGlassDevice.getInstance()?.glassInfo?.typeId}
                        """.trimIndent()
            info.postValue(str)

        }?:run{
            info.postValue(null)
        }
    }

    fun getSensor(v: View){//show or dismiss sensors value.
        if (sensorValue.value == R.string.get_sensor_values){
            sensorValue.postValue(R.string.stop_get_sensor_values)
        }else{
            sensorValue.postValue(R.string.get_sensor_values)
        }
    }

    fun doRecordVoice(v: View){//action of mic clicked
        action(HardwareEvent.Mic)
    }

}

fun FloatArray?.showString():String{//format float[] value.
    var str ="{"
    this?.forEachIndexed { index, fl ->
        str = "$str\nindex[$index]:${String.format("%.6f", fl)}"
    }
    str = "$str\n}"
    return str
}
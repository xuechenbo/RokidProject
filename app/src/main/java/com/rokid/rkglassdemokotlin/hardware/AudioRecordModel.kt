package com.rokid.rkglassdemokotlin.hardware

import android.annotation.SuppressLint
import android.media.*
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.rokid.logger.RKLogger
import com.rokid.rkglassdemokotlin.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*

/**
 * Audio record model
 * Data model for Audio record view, for this is a test sample, only one file will be created.
 * @property audioRecordFile file to record or display
 * @property showRecording
 * @property showPlayLast if file is exit and is not in recording time play last button will be shown on screen
 * @property showRecordBtn if is not in playing mode, record button will ben shown on screen
 * @property canPlay
 * @property recordSrc shown on record button
 * @property recordingTime times recorded
 * @property action
 */
class AudioRecordModel(
    val audioRecordFile: File,
    val showRecording: MutableLiveData<Boolean> = MutableLiveData(),
    val showPlayLast: MutableLiveData<Boolean> = MutableLiveData(),
    val showRecordBtn: MutableLiveData<Boolean> = MutableLiveData(),
    val canPlay: MutableLiveData<Boolean> = MutableLiveData(),
    val recordSrc: MutableLiveData<Int> = MutableLiveData(),
    val recordingTime: MutableLiveData<String> = MutableLiveData(),
    val action: (Int) -> Unit
) {

    private val AUDIO_SAMPLE_SIZE = 16000
    private val AUDIO_SOURCE = MediaRecorder.AudioSource.MIC
    private val AUDIO_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO
    private val AUDIO_CHANNEL_CONFIG_OUT = AudioFormat.CHANNEL_OUT_STEREO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private val AUDIO_MIN_BUFFER_SIZE =
        AudioRecord.getMinBufferSize(AUDIO_SAMPLE_SIZE, AUDIO_CHANNEL_CONFIG, AUDIO_FORMAT)
    private var recorder: AudioRecord? = null

    @SuppressLint("MissingPermission")
    fun startRecording() {
        recorder ?: run {
            recorder = AudioRecord(
                AUDIO_SOURCE,
                AUDIO_SAMPLE_SIZE,
                AUDIO_CHANNEL_CONFIG,
                AUDIO_FORMAT,
                AUDIO_MIN_BUFFER_SIZE
            )

        }
        recorder?.startRecording()

        object : Thread() {
            val startTime = System.currentTimeMillis()

            var toReset = 0
            override fun run() {
                super.run()
                while (recorder != null) {
                    val count = System.currentTimeMillis() - startTime
                    if (toReset > 10) {
                        showRecording.postValue(showRecording.value != true)
                        toReset = 0
                    }
                    toReset++
                    recordingTime.postValue(String.format("%02d'%02d", count / 1000, count % 1000))
                    sleep(50)
                }
                showRecording.postValue(true)
            }
        }.start()

        object : Thread() {

            var toNext: Boolean = true

            override fun run() {
                super.run()
                RKLogger.e("file path = ${audioRecordFile.absolutePath}")
                val buffer = ByteArray(AUDIO_MIN_BUFFER_SIZE)
                if (audioRecordFile.parentFile?.exists() != true) {
                    audioRecordFile.mkdirs()
                }

                val fos = FileOutputStream(audioRecordFile)

                while (toNext) {
                    val length = recorder?.read(buffer, 0, AUDIO_MIN_BUFFER_SIZE)
                    if (length == AudioRecord.ERROR_INVALID_OPERATION || length == AudioRecord.ERROR_BAD_VALUE) {
                        continue
                    }
                    fos.write(buffer)
                    fos.flush()
                    toNext = recorder?.let {
                        true
                    } ?: run {
                        false
                    }
                }
                fos.close()
            }
        }.start()
    }

    private fun releaseRecorder() {
        recorder?.stop()
        recorder?.release()
        recorder = null
    }


    fun onRecord(v: View) {
        if (recordSrc.value == R.string.start_record) {
            recordSrc.postValue(R.string.stop_record)
            showPlayLast.postValue(null)
            startRecording()
        } else {
            releaseRecorder()
            recordSrc.postValue(R.string.start_record)
            if (audioRecordFile.exists()) {
                showPlayLast.postValue(true)
                canPlay.postValue(true)
            } else {
                showPlayLast.postValue(null)
                canPlay.postValue(false)
            }
        }
    }

    fun onPlay(v: View) {
        canPlay.postValue(false)
        canPlay.observeForever {
        }
        object : Thread() {
            val startTime = System.currentTimeMillis()

            var toReset = 0
            override fun run() {
                super.run()
                while (true) {
                    val count = System.currentTimeMillis() - startTime
                    if (toReset > 10) {
                        showRecording.postValue(showRecording.value != true)
                        toReset = 0
                    }
                    toReset++
                    recordingTime.postValue(String.format("%02d'%03d", count / 1000, count % 1000))

                    sleep(50)
                    if (canPlay.value == true){
                        break
                    }
                }
                showRecording.postValue(true)
            }
        }.start()

        object : Thread(){
            override fun run() {
                super.run()

                val dis = DataInputStream(BufferedInputStream(FileInputStream(audioRecordFile)))

                val bufferSizeInByte = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_SIZE, AUDIO_CHANNEL_CONFIG_OUT, AUDIO_FORMAT)

                val player = AudioTrack(AudioManager.STREAM_MUSIC, AUDIO_SAMPLE_SIZE, AUDIO_CHANNEL_CONFIG_OUT, AUDIO_FORMAT, bufferSizeInByte, AudioTrack.MODE_STREAM)

                val bytes = ByteArray(bufferSizeInByte)
                player.play()
                player.audioAttributes
                while (true){
                    var i = 0
                    while (dis.available() > 0 && i < bytes.size){
                        bytes[i] = dis.readByte()
                        i++
                    }

                    player.write(bytes, 0, bytes.size)
                    if (i != bufferSizeInByte){
                        player.stop()
                        player.release()
                        canPlay.postValue(true)
                        break
                    }
                }
            }
        }.start()


    }

    fun onExit(v: View) {
        releaseRecorder()
        action(-1)
    }


}
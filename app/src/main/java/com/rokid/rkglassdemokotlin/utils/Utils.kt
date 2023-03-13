package com.rokid.rkglassdemokotlin.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.rokid.rkglassdemokotlin.camera.FaceModel
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink

class Utils {
    companion object {
        fun IsEquest(it: List<FaceModel>, mFacelList: MutableList<FaceModel>): Boolean {
            return it.sumOf { it.faceId } == mFacelList.sumOf { it.faceId }
        }

        fun base64ToBitmap(base64Data: String?): Bitmap? {
            val bytes = Base64.decode(base64Data, Base64.NO_WRAP)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }
}

class BitmapRequestBody(
    private val bitmap: Bitmap,
    private val format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
) : RequestBody() {
    override fun contentType(): MediaType? {
        return when (format) {
            Bitmap.CompressFormat.WEBP -> "image/webp"
            Bitmap.CompressFormat.PNG -> "image/png"
            else -> "image/jpeg"
        }.toMediaTypeOrNull()
    }

    override fun writeTo(sink: BufferedSink) {
        bitmap.compress(format, 100, sink.outputStream())
    }
}
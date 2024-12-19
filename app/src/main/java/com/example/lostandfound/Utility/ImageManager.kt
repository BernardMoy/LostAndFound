package com.example.lostandfound.Utility

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import java.io.ByteArrayOutputStream

object ImageManager {
    fun uriToBitmap(context: Context, imageUri: Uri): ByteArray{
        val source = ImageDecoder.createSource(context.contentResolver, imageUri)
        val bitmap = ImageDecoder.decodeBitmap(source)
        val outputStream = ByteArrayOutputStream()

        // compress the image
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }
}
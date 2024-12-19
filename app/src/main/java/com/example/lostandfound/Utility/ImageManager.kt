package com.example.lostandfound.Utility

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object ImageManager {
    @OptIn(ExperimentalEncodingApi::class)
    fun uriToString(context: Context, imageUri: Uri?): String{
        if (imageUri == null){
            return ""
        }

        val byteArray = context.contentResolver.openInputStream(imageUri)?.use { it.buffered().readBytes() }

        // convert byte array to string
        if (byteArray != null) {
            return Base64.encode(byteArray)
        } else {
            return ""
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun stringToUri(context: Context, string: String): Uri? {
        // if string is empty, return null
        if (string == ""){
            return null
        }

        // decode the string of bytearray
        val decodedBytes = Base64.decode(string)

        // store in device's cache memory
        val avatarFile = File(context.cacheDir, "user_avatar.png")
        val fileOutputStream = FileOutputStream(avatarFile)
        fileOutputStream.write(decodedBytes)
        fileOutputStream.close()

        // Return the URI from the cache file
        return Uri.fromFile(avatarFile)
    }
}
package com.example.lostandfound.TFLiteManager

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Glide
import com.example.lostandfound.ml.SiameseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

/*
val modelFile = context.assets.open(modelFileName).use { it.readBytes() }
        interpreter = Interpreter(modelFile)
 */

interface PredictCallback {
    fun onComplete(distance: Float)
}

class ImageClassifier(private val context: Context) {
    val model = SiameseModel.newInstance(context)
    private val imageSize = 224

    private fun predictBitmap(image1: Bitmap, image2: Bitmap): Float {

        // Creates inputs for reference.
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
        val inputFeature1 =
            TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)

        // Create byte buffer from bitmap, then load the byte buffers into the input features
        val byteBuffer1: ByteBuffer = bitmapToByteBuffer(image1)
        inputFeature0.loadBuffer(byteBuffer1)
        val byteBuffer2: ByteBuffer = bitmapToByteBuffer(image2)
        inputFeature1.loadBuffer(byteBuffer2)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0, inputFeature1)
        val result = outputs.outputFeature0AsTensorBuffer.floatArray[0]

        // return the predicted distance
        return result
    }

    /*
    Glide must be used here to load images, otherwise images on firebase storage and not on your device will not be loaded properly
    Glide.with(context)... requires execution on a background thread as it blocks main thread
    Hence this function must be asynchronous and returned with a callback instead
     */
    fun predict(image1: Uri, image2: Uri, callback: PredictCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            // convert uri to bitmap
            // this blocks main thread so a callback function is needed
            val image1bm =
                Glide.with(context).asBitmap().load(image1).override(1080, 1080).submit().get()
            val image2bm =
                Glide.with(context).asBitmap().load(image2).override(1080, 1080).submit().get()

            // resize the bitmap to the size (224 224) accepted by the model
            val image1bmResized = Bitmap.createScaledBitmap(image1bm, imageSize, imageSize, false)
            val image2bmResized = Bitmap.createScaledBitmap(image2bm, imageSize, imageSize, false)

            // make prediction
            val distance = predictBitmap(image1bmResized, image2bmResized)

            // return the result in main thread
            withContext(Dispatchers.Main) {
                callback.onComplete(distance)
            }
        }
    }

    private suspend fun resizeBitmap(image: Bitmap): Bitmap {
        return withContext(Dispatchers.Default) {
            Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
        }
    }

    // method to close the model after finish using it
    fun close() {
        model.close()
    }


    // utility method to convert bitmap to bytebuffer
    private fun bitmapToByteBuffer(img: Bitmap): ByteBuffer {
        val byteBuffer: ByteBuffer =
            ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)  // 4 for float, 3 for rgb channels
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(imageSize * imageSize)
        img.getPixels(intValues, 0, img.width, 0, 0, img.width, img.height)

        var pixel: Int = 0
        for (i in 0 until imageSize) {
            for (j in 0 until imageSize) {
                val value: Int = intValues[pixel++]
                // get the rgb values and then normalise them to between 0 and 1
                val red = (value shr 16) and 0xFF
                val green = (value shr 8) and 0xFF
                val blue = value and 0xFF
                byteBuffer.putFloat(red / 255f)
                byteBuffer.putFloat(green / 255f)
                byteBuffer.putFloat(blue / 255f)
            }
        }

        return byteBuffer
    }
}
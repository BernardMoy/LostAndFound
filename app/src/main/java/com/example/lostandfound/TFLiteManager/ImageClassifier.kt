package com.example.lostandfound.TFLiteManager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import com.example.lostandfound.ml.Siamesemodel224
import org.tensorflow.lite.DataType
import org.tensorflow.lite.TensorFlowLite
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

/*
val modelFile = context.assets.open(modelFileName).use { it.readBytes() }
        interpreter = Interpreter(modelFile)
 */


class ImageClassifier (private val context: Context){
    val model = Siamesemodel224.newInstance(context)
    val imageSize = 224

    fun predictBitmap(image1: Bitmap, image2: Bitmap): Float{

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
        val inputFeature1 = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)

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

    fun predict(image1: Uri, image2: Uri): Float{
        // convert the uris to bitmaps
        val image1src = ImageDecoder.createSource(context.contentResolver, image1)
        val image2src = ImageDecoder.createSource(context.contentResolver, image2)
        val image1bm = ImageDecoder.decodeBitmap(image1src){decoder, _, _ ->
            decoder.isMutableRequired = true
        }
        val image2bm = ImageDecoder.decodeBitmap(image2src){decoder, _, _ ->
            decoder.isMutableRequired = true
        }

        // resize the bitmaps to the size required by the model
        val image1bmResized = Bitmap.createScaledBitmap(image1bm, imageSize, imageSize, false)
        val image2bmResized = Bitmap.createScaledBitmap(image2bm, imageSize, imageSize, false)

        return predictBitmap(image1bmResized, image2bmResized)
    }

    // method to close the model after finish using it
    fun close(){
        model.close()
    }


    // utility method to convert bitmap to bytebuffer
    fun bitmapToByteBuffer(img: Bitmap): ByteBuffer{
        val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(4*imageSize*imageSize*3)  // 4 for float, 3 for rgb channels
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(imageSize * imageSize)
        img.getPixels(intValues, 0, img.width, 0, 0, img.width, img.height)

        var pixel: Int = 0
        for (i in 0 until imageSize){
            for (j in 0 until imageSize){
                val value: Int = intValues[pixel++]
                // get the rgb values and then normalise them to between 0 and 1
                val red = (value shr 16) and 0xFF
                val green = (value shr 8) and 0xFF
                val blue = value and 0xFF
                byteBuffer.putFloat(red/255f)
                byteBuffer.putFloat(green/255f)
                byteBuffer.putFloat(blue/255f)
            }
        }

        return byteBuffer
    }
}
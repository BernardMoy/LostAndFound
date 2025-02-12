package com.example.lostandfound.TFLiteManager

import android.content.Context
import android.net.Uri
import com.example.lostandfound.ml.SiameseModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.TensorFlowLite
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

/*
val modelFile = context.assets.open(modelFileName).use { it.readBytes() }
        interpreter = Interpreter(modelFile)
 */

class ImageClassifier (context: Context){
    val model = SiameseModel.newInstance(context)

    fun predict_bytebuffer(image1: ByteBuffer, image2: ByteBuffer): Float{

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 448, 448, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(image1)
        val inputFeature1 = TensorBuffer.createFixedSize(intArrayOf(1, 448, 448, 3), DataType.FLOAT32)
        inputFeature1.loadBuffer(image2)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0, inputFeature1)
        val result = outputs.outputFeature0AsTensorBuffer.floatArray[0]

        // return the predicted distance
        return result
    }

    fun predict(image1: Uri, image2: Uri): Float{
        return 0.0f
    }

    // method to close the model after finish using it
    fun close(){
        model.close()
    }

}
package com.example.lostandfound.ui.PushNotificationsTest

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lostandfound.FirebaseManagers.FCMApi
import com.example.lostandfound.FirebaseManagers.MessageBody
import com.example.lostandfound.FirebaseManagers.NotificationBody
import com.example.lostandfound.FirebaseManagers.SendMessageDto
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.io.FileInputStream

class PushNotificationsTestViewModel : ViewModel() {
    val fcmToken: MutableState<String> = mutableStateOf("")
    val userID: MutableState<String> = mutableStateOf("")
    val title: MutableState<String> = mutableStateOf("")
    val content: MutableState<String> = mutableStateOf("")


    private val fcmApi: FCMApi = Retrofit.Builder()
        .baseUrl("https://fcm.googleapis.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(FCMApi::class.java)

    fun sendNotification(context: Context){
        viewModelScope.launch(Dispatchers.IO){
            val adminToken = getAccessToken(context)

            val messageDto = SendMessageDto(
                message = MessageBody(
                    token = fcmToken.value,
                    notification = NotificationBody(
                        title = title.value,
                        body = content.value
                    )
                )
            )

            try {
                fcmApi.sendMessage("Bearer $adminToken", messageDto)
                Log.d("PUSH NOTIF", "Push notification sent")
            } catch (e: HttpException){
                e.printStackTrace()
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }


    fun getAccessToken(context: Context): String {
        val credentials = GoogleCredentials
            .fromStream(
                context.assets.open("service-account.json")
            )
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))

        credentials.refreshIfExpired()
        return credentials.accessToken.tokenValue
    }
}
package com.example.lostandfound.ui.PushNotificationsTest

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lostandfound.PushNotificationManagers.MessageBody
import com.example.lostandfound.PushNotificationManagers.NotificationBody
import com.example.lostandfound.PushNotificationManagers.SendMessageDto
import com.example.lostandfound.PushNotificationManagers.PushNotificationManager.fcmApi
import com.example.lostandfound.PushNotificationManagers.PushNotificationManager.getAccessToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class PushNotificationsTestViewModel : ViewModel() {
    val fcmToken: MutableState<String> = mutableStateOf("")
    val title: MutableState<String> = mutableStateOf("")
    val content: MutableState<String> = mutableStateOf("")

    fun sendNotification(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val adminToken = getAccessToken(context)

            // create the message dto object for the contents of the push notifications
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
                Log.d("Push notification", "Push notification sent")

            } catch (e: HttpException) {
                e.printStackTrace()

            } catch (e: IOException) {
                e.printStackTrace()

            }
        }
    }
}
package com.example.lostandfound.Integration

import android.content.Context
import android.support.test.uiautomator.By
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.Until
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lostandfound.PushNotificationManagers.PushNotificationCallback
import com.example.lostandfound.PushNotificationManagers.PushNotificationManager
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/*
IMPORTANT -- Launch the app and ENABLE NOTIFICATION PERMISSIONS FIRST
Otherwise THIS TEST WILL FAIL
 */
class PushNotificationTest : FirebaseMessagingService() {
    private lateinit var device: UiDevice
    private lateinit var context: Context

    @Before
    fun setUp() {
        // set up the device
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // set up the app context
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testSendPushNotification() {
        // first get the FCM token of the current device
        var token = ""
        val latch = CountDownLatch(1)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    fail("Failed generating FCM token")
                    latch.countDown()
                    return@addOnCompleteListener
                }
                token = task.result
                latch.countDown()
            }
        latch.await(60, TimeUnit.SECONDS)

        // now send push notifs to the current device
        val latch2 = CountDownLatch(1)
        PushNotificationManager.sendPushNotification(
            context = context,
            fcmToken = token,
            title = "TestTitle",
            content = "TestContent",
            callback = object : PushNotificationCallback {
                override fun onComplete(success: Boolean) {
                    if (!success) {
                        fail("Failed sending push notification")
                        latch2.countDown()
                        return
                    }
                    latch2.countDown()
                }
            }
        )
        latch2.await(60, TimeUnit.SECONDS)
        Thread.sleep(5000)

        // now assert that the notification appeared
        device.openNotification()
        device.wait(Until.hasObject(By.text("TestTitle")), 5000)
        val notification = device.findObject(By.text("TestTitle"))

        assertNotNull(notification)
    }
}
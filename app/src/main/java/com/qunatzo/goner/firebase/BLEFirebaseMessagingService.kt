package com.qunatzo.goner.firebase

import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.qunatzo.goner.R
import com.qunatzo.goner.deviceBLE.getTelephoneUUID


class BLEFirebaseMessagingService: FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        val dd = getTelephoneUUID(this)
        FirebaseMessaging.getInstance().subscribeToTopic(getTelephoneUUID(this).toString())

    }


    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        val data = p0?.data
        val notification = NotificationCompat.Builder(this, "lost_devices")
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(data?.get("title").toString())
                .setContentText(data?.get("text").toString())
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)

    }
}
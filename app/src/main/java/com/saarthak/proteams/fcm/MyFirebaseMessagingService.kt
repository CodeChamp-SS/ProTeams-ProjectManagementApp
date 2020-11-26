package com.saarthak.proteams.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.saarthak.proteams.MainActivity
import com.saarthak.proteams.R
import com.saarthak.proteams.SigninActivity
import com.saarthak.proteams.constants.Constants.FCM_KEY_MSG
import com.saarthak.proteams.constants.Constants.FCM_KEY_TITLE
import com.saarthak.proteams.util.FireStoreClass

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val TAG = "FireBaseMessage"
        const val PENDING_INTENT_REQ_CODE = 7
    }

    private fun sendTokenToServer(token: String) {
        // todo
    }

    private fun sendNotification(msg: String, title: String) {
        val intent = if (FireStoreClass().getCurUserId().isNotEmpty()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, SigninActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent = PendingIntent.getActivity(
            this,
            PENDING_INTENT_REQ_CODE,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = resources.getString(R.string.default_notification_channel_id)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setContentTitle(title)
            .setContentText(msg)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Trello Clone Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onMessageReceived(rMsg: RemoteMessage) {
        super.onMessageReceived(rMsg)
        Log.d(TAG, "onMessageReceived: ${rMsg.from}")

        if (rMsg.data.isNotEmpty()) {
            Log.d(TAG, "MessageData: ${rMsg.data}")

            val title = rMsg.data[FCM_KEY_TITLE]!!
            val msg = rMsg.data[FCM_KEY_MSG]!!

            sendNotification(msg, title)
        }

        rMsg.notification?.let {
            Log.d(TAG, "Notification: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

        sendTokenToServer(token)
    }
}
package com.hensof.noteplay.frepos.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hensof.noteplay.HenNotesActivity
import com.hensof.noteplay.R
import com.hensof.noteplay.frepos.presentation.app.HenNotesApplication

private const val HEN_NOTES_CHANNEL_ID = "hen_notes_notifications"
private const val HEN_NOTES_CHANNEL_NAME = "HenNotes Notifications"
private const val HEN_NOTES_NOT_TAG = "HenNotes"

class HenNotesPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                henNotesShowNotification(it.title ?: HEN_NOTES_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                henNotesShowNotification(it.title ?: HEN_NOTES_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            henNotesHandleDataPayload(remoteMessage.data)
        }
    }

    private fun henNotesShowNotification(title: String, message: String, data: String?) {
        val henNotesNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                HEN_NOTES_CHANNEL_ID,
                HEN_NOTES_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            henNotesNotificationManager.createNotificationChannel(channel)
        }

        val henNotesIntent = Intent(this, HenNotesActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val henNotesPendingIntent = PendingIntent.getActivity(
            this,
            0,
            henNotesIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val henNotesNotification = NotificationCompat.Builder(this, HEN_NOTES_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_hen_notes_noti)
            .setAutoCancel(true)
            .setContentIntent(henNotesPendingIntent)
            .build()

        henNotesNotificationManager.notify(System.currentTimeMillis().toInt(), henNotesNotification)
    }

    private fun henNotesHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}
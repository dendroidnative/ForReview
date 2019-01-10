package com.forreview.datamanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.forreview.R
import com.forreview.ui.main.MainActivity
import timber.log.Timber

private const val CHANNEL_ID_MEDITATION = "CHANNEL_ID_MEDITATION"
private const val ID_NOTIFICATION_MEDITATION = 1

class MyNotificationManager(val context: Context) {

    init {
        createNotificationChannel()
    }

    fun showMeditationNotification(notificationText: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID_MEDITATION)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Alert")
            .setContentText(notificationText)
            .setContentIntent(getIntent())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(ID_NOTIFICATION_MEDITATION, builder.build())

        Timber.tag("AlarmReceiver").d("showMeditationNotification")
    }

    private fun getIntent(): PendingIntent {
        val meditationIntent = Intent(context, MainActivity::class.java)

        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(meditationIntent)
            return@run getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_meditation_channel_name)
            val description = context.getString(R.string.notification_meditation_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_MEDITATION, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
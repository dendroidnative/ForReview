package com.forreview.datamanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.forreview.AlarmReceiver
import com.forreview.model.Reminder
import timber.log.Timber
import java.text.SimpleDateFormat

private const val RC_MEDITATION_REMINDER = 1

class MyAlarmManager(val context: Context) {

    fun setMeditationReminder(reminder: Reminder) {
        cancelMeditationAlarm()
        if (reminder.isSet) {
            setMeditationAlarm(reminder)
        }
    }

    private fun setMeditationAlarm(reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = getMeditationAlarmIntent(reminder)

        Timber.tag("AlarmReceiver").d("setMeditationAlarm ${SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(reminder.timeMillis)}")
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            reminder.timeMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
    }

    private fun getMeditationAlarmIntent(reminder: Reminder? = null) = Intent(context, AlarmReceiver::class.java).let { intent ->
        reminder?.also {
            intent.putExtra(AlarmReceiver.EXTRA_REMINDER_DAYS, reminder.days.name)
        }
        return@let PendingIntent.getBroadcast(context,
            RC_MEDITATION_REMINDER, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun cancelMeditationAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getMeditationAlarmIntent())
    }
}
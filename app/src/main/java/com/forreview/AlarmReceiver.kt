package com.forreview

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.forreview.datamanager.DataManager
import com.forreview.datamanager.MyNotificationManager
import com.forreview.helper.CoroutineExecutor
import com.forreview.model.Meditation
import com.forreview.model.MeditationAction
import com.forreview.model.MeditationsResult
import com.forreview.model.Reminder
import com.forreview.utils.launchSilent
import com.forreview.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber
import java.util.*
import kotlin.random.Random

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val myNotificationManager by inject<MyNotificationManager>()
    private val dataManager by inject<DataManager>()
    private val executor by inject<CoroutineExecutor>()

    private var disposable: Disposable? = null

    override fun onReceive(context: Context, intent: Intent) {
        disposable = dataManager.activeMeditationObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is MeditationsResult.ActiveMeditation -> {
                        when (it) {
                            is MeditationsResult.ActiveMeditation.Success -> {
                                prepareNotification(it.meditation, intent.getStringExtra(EXTRA_REMINDER_DAYS), context)
                            }
                        }
                    }
                }
            }

        loadCurrentMeditation()
    }

    private fun prepareNotification(
        meditation: Meditation,
        reminderDays: String,
        context: Context
    ) {
        val notificationString =
            if (meditation.notification.isEmpty()) getRandomString(context) else meditation.notification
        Timber.e("$notificationString")
        try {
            val days = Reminder.Days.valueOf(reminderDays)

            Timber.tag("AlarmReceiver").i("onReceive $days; bundle $reminderDays")

            when (days) {
                Reminder.Days.WEEKDAYS -> {
                    val calendar = Calendar.getInstance()
                    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                    if (dayOfWeek != Calendar.SATURDAY || dayOfWeek != Calendar.SUNDAY) {
                        showNotification(notificationString)
                    }
                }
                Reminder.Days.DAILY -> {
                    showNotification(notificationString)
                }
            }
        } catch (exc: Exception) {
            Timber.e(exc)
        }
    }

    private fun loadCurrentMeditation() = launchSilent(executor.ui()) {
        dataManager.processAction(MeditationAction.LoadActiveMeditation(true))
    }

    private fun getRandomString(context: Context): String {
        val stringArray = context.resources.getStringArray(R.array.alarm_random_notification)
        val index = Random.nextInt(stringArray.size - 1)
        return stringArray[index]
    }

    private fun showNotification(notificationString: String) {
        myNotificationManager.showMeditationNotification(notificationString)
        disposable?.dispose()
    }

    companion object {

        const val EXTRA_REMINDER_DAYS = "EXTRA_REMINDER_DAYS"
    }


}

package com.forreview

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.forreview.datamanager.DataManager
import com.forreview.helper.CoroutineExecutor
import com.forreview.model.MeditationAction
import com.forreview.utils.launchSilent
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber

class DateChangeReceiver : BroadcastReceiver(), KoinComponent {

    private val dataManager by inject<DataManager>()

    private val executor by inject<CoroutineExecutor>()

    override fun onReceive(context: Context, intent: Intent) = launchSilent(executor.ui()) {
        Timber.d("onReceive ${intent.action}")
        if (intent.action == Intent.ACTION_DATE_CHANGED) {
            dataManager.processAction(MeditationAction.IsMeditationAvailable(true))
        }
    }
}

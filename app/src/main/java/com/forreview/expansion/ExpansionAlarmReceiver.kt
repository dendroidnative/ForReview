package com.forreview.expansion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller
import timber.log.Timber

class ExpansionAlarmReceiver : BroadcastReceiver() {

    init {
        Timber.tag("DownloaderActivity").d("ExpansionAlarmReceiver init")
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val res = DownloaderClientMarshaller.startDownloadServiceIfRequired(context, intent, ExpansionService::class.java)
            Timber.d("onReceive $res")
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e)
        }
    }
}

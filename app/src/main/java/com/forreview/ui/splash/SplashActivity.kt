package com.forreview.ui.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import com.forreview.base.BaseActivity
import com.forreview.datamanager.DataManager
import com.forreview.expansion.DownloaderActivity
import com.forreview.helper.CoroutineExecutor
import com.forreview.helper.PrefsHelper
import com.forreview.helper.ResByName
import com.forreview.ui.main.MainActivity
import com.forreview.ui.tutorial.TutorialActivity
import com.forreview.utils.launchSilent
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class SplashActivity : BaseActivity() {

    private val prefsHelper by inject<PrefsHelper>()

    private val appExecutors by inject<CoroutineExecutor>()

    private val dataManager by inject<DataManager>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!ResByName.isExpansionFilesUnpacked(this)) {
            DownloaderActivity.startForResult(this,
                RC_DOWNLOAD_EXPANSION
            )
        } else {
            init()
        }
//        DownloaderActivity.startForResult(this, RC_DOWNLOAD_EXPANSION)
    }

    private fun init() = launchSilent(appExecutors.ui()) {

        if (prefsHelper.isFirstLaunch()) {
            seedDataBase()
            prefsHelper.setFirstLaunch(false)
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                TutorialActivity.start(this@SplashActivity)
            }
        } else {
//            seedDataBase()//TODO remove this line
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                MainActivity.start(this@SplashActivity)
//                TutorialActivity.start(this@SplashActivity)
            }
        }
        finish()
    }

    private suspend fun seedDataBase() = withContext(appExecutors.io()) {
        dataManager.seedDataBase()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_DOWNLOAD_EXPANSION) {
            if (resultCode == Activity.RESULT_OK) {
                init()
            } else {
                finish()
            }
        }
    }

    companion object {
        const val RC_DOWNLOAD_EXPANSION = 1
    }
}

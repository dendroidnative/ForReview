package com.forreview.expansion

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings.ACTION_WIFI_SETTINGS
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.vending.expansion.downloader.DownloadProgressInfo
import com.google.android.vending.expansion.downloader.Helpers
import com.forreview.R
import com.forreview.helper.GlideHelper
import com.forreview.model.ViewState
import kotlinx.android.synthetic.main.activity_downloader.*
import timber.log.Timber

sealed class DownloaderViewState: ViewState {

    object Success: DownloaderViewState()
    object Error: DownloaderViewState()

    sealed class Download: DownloaderViewState() {
        data class Idle(val status: String): Download()
        data class Connecting(val status: String): Download()
        data class Downloading(val status: String): Download()
        data class Failed(val status: String): Download()
        data class CellularPermission(val status: String): Download()
        data class PausedByRequest(val status: String): Download()
        data class PausedRoaming(val status: String): Download()
        data class Complete(val status: String): Download()
        data class Progress(val progress: DownloadProgressInfo): Download()
    }

    sealed class Validating: DownloaderViewState() {
        data class Progress(val progress: DownloadProgressInfo): Validating()
        object Success: Validating()
        object Error: Validating()
    }
}

class DownloaderActivity : AppCompatActivity() {

    private var mStatePaused: Boolean = false

    private var service: DownloaderService? = null

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            Timber.tag("DownloaderService").d("DownloaderActivity onServiceConnected")
            service = (binder as DownloaderService.LocalBinder).service
            service?.liveData?.observe(this@DownloaderActivity, stateObserver)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Timber.tag("DownloaderService").d("DownloaderActivity onServiceDisconnected")
        }
    }

    private val stateObserver = Observer<DownloaderViewState> { state ->
        when(state) {
            is DownloaderViewState.Success -> {
//                setResult(Activity.RESULT_OK)
//                finish()
            }
            is DownloaderViewState.Error -> {
//                setResult(Activity.RESULT_CANCELED)
//                finish()
            }
            is DownloaderViewState.Download -> {
                when(state) {
                    is DownloaderViewState.Download.Idle -> {
                        setState(state.status)

                        downloaderDashboard.visibility = View.VISIBLE
                        approveCellular.visibility = View.GONE
                        progressBar.isIndeterminate = true
                        setButtonPausedState(false)
                    }
                    is DownloaderViewState.Download.Connecting -> {
                        setState(state.status)

                        downloaderDashboard.visibility = View.VISIBLE
                        approveCellular.visibility = View.GONE
                        progressBar.isIndeterminate = true
                        setButtonPausedState(false)
                    }
                    is DownloaderViewState.Download.Downloading -> {
                        setState(state.status)

                        downloaderDashboard.visibility = View.VISIBLE
                        approveCellular.visibility = View.GONE
                        progressBar.isIndeterminate = false
                        setButtonPausedState(false)
                    }
                    is DownloaderViewState.Download.Failed -> {
                        setState(state.status)

                        downloaderDashboard.visibility = View.GONE
                        approveCellular.visibility = View.GONE
                        progressBar.isIndeterminate = false
                        setButtonPausedState(true)
                    }
                    is DownloaderViewState.Download.CellularPermission -> {
                        setState(state.status)

                        downloaderDashboard.visibility = View.GONE
                        approveCellular.visibility = View.VISIBLE
                        progressBar.isIndeterminate = false
                        setButtonPausedState(true)
                    }
                    is DownloaderViewState.Download.PausedByRequest -> {
                        setState(state.status)

                        downloaderDashboard.visibility = View.VISIBLE
                        approveCellular.visibility = View.GONE
                        progressBar.isIndeterminate = false
                        setButtonPausedState(true)
                    }
                    is DownloaderViewState.Download.PausedRoaming -> {
                        setState(state.status)

                        downloaderDashboard.visibility = View.VISIBLE
                        approveCellular.visibility = View.GONE
                        progressBar.isIndeterminate = false
                        setButtonPausedState(true)
                    }
                    is DownloaderViewState.Download.Complete -> {
                        setState(state.status)

                        downloaderDashboard.visibility = View.GONE
                        approveCellular.visibility = View.GONE
                        progressBar.isIndeterminate = false
                        setButtonPausedState(false)
                    }
                    is DownloaderViewState.Download.Progress -> {
                        setProgress(state.progress)
                    }
                }
            }
            is DownloaderViewState.Validating -> {
                downloaderDashboard.visibility = View.VISIBLE
                approveCellular.visibility = View.GONE
                statusText.setText(R.string.expansion_activity_text_verifying_download)
                pauseButton.setOnClickListener {
                    service?.cancelValidation()
                }
                pauseButton.setText(R.string.expansion_activity_text_button_cancel_verify)

                when(state) {
                    is DownloaderViewState.Validating.Progress -> {
                        setProgress(state.progress)
                    }
                    is DownloaderViewState.Validating.Success -> {
                        setResult(Activity.RESULT_OK)
                        finish()
//                        downloaderDashboard.visibility = View.VISIBLE
//                        approveCellular.visibility = View.GONE
//                        statusText.setText(R.string.expansion_activity_text_validation_complete)
//                        pauseButton.setOnClickListener {
//                            setResult(Activity.RESULT_OK)
//                            finish()
//                        }
//                        pauseButton.setText(android.R.string.ok)
                    }
                    is DownloaderViewState.Validating.Error -> {
                        downloaderDashboard.visibility = View.VISIBLE
                        approveCellular.visibility = View.GONE
                        statusText.setText(R.string.expansion_activity_text_validation_failed)
                        pauseButton.setOnClickListener {
                            setResult(Activity.RESULT_CANCELED)
                            finish()
                        }
                        pauseButton.setText(android.R.string.ok)
                    }
                }
            }
        }
    }

    private fun setProgress(progress: DownloadProgressInfo) {
        progressAverageSpeed.text = getString(
            R.string.kilobytes_per_second,
            Helpers.getSpeedString(progress.mCurrentSpeed)
        )
        progressTimeRemaining.text = getString(
            R.string.time_remaining,
            Helpers.getTimeRemaining(progress.mTimeRemaining)
        )

        progress.mOverallTotal = progress.mOverallTotal;
        progressBar.max = (progress.mOverallTotal).toInt()
        progressBar.progress = (progress.mOverallProgress).toInt()
        progressAsPercentage.text = "${progress.mOverallProgress * 100 / progress.mOverallTotal}%"
        progressAsFraction.text = Helpers.getDownloadProgressString(progress.mOverallProgress, progress.mOverallTotal)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloader)

        GlideHelper.loadBg(this, root, R.drawable.bg_splash)

        pauseButton.setOnClickListener {
            if (mStatePaused) {
                service?.requestContinueDownload()
            } else {
                service?.requestPauseDownload()
            }
            setButtonPausedState(!mStatePaused)
        }

        wifiSettingsButton.setOnClickListener {
            startActivity(Intent(ACTION_WIFI_SETTINGS))
        }

        resumeOverCellular.setOnClickListener {
            service?.requestContinueDownloadWithCellular()
            approveCellular.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        DownloaderService.bind(this, serviceConnection)
    }

    override fun onPause() {
        super.onPause()
        service = null
        DownloaderService.unbind(this, serviceConnection)
    }

    private fun setState(state: String) {
        statusText.setText(state)
    }

    private fun setButtonPausedState(paused: Boolean) {
        mStatePaused = paused
        val stringResourceID = if (paused)
            R.string.expansion_activity_text_button_resume
        else
            R.string.expansion_activity_text_button_pause
        pauseButton.setText(stringResourceID)
    }

    companion object {

        fun startForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult(Intent(activity, DownloaderActivity::class.java), requestCode)
        }
    }
}

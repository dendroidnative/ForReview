package com.forreview.ui.meditation.meditation

import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_MUSIC
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PowerManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.forreview.BuildConfig
import com.forreview.R
import com.forreview.base.BaseFragment
import com.forreview.helper.ResByName
import com.forreview.model.ViewState
import com.forreview.ui.meditation.main.ActiveMeditationViewState
import com.forreview.ui.meditation.main.MeditationEvent
import com.forreview.ui.meditation.main.MeditationMainViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.meditation_fragment.*
import org.apache.commons.lang3.time.DurationFormatUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.concurrent.TimeUnit

private const val ACTION_PAID = "com.forreview.ui.meditation.ACTION_PAID"

class MeditationFragment : BaseFragment(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private var mediaPlayer: MediaPlayer? = null

    private var audioTimerDisposable: Disposable? = null

    private val audioTimerObservable = Observable.interval(300, TimeUnit.MILLISECONDS)

    private val viewModel by viewModel<MeditationMainViewModel>()

    init {
        Timber.e("init")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.meditation_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stopBtn.visibility = View.GONE
        playBtn.visibility = View.GONE
        pauseBtn.visibility = View.GONE
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)

        if (activity?.intent?.action != ACTION_PAID) {
            subscribe(viewModel.meditationObservable)
        } else {
            subscribe(viewModel.paidMeditationObservable)
        }

//        viewModel.onCompleteAction.observe(viewLifecycleOwner, Observer {
//            MeditationNavigation.fromMeditationToWakeup(activity)
//        })

//        viewModel.onStopAction.observe(viewLifecycleOwner, Observer {
//            MeditationNavigation.popToStages(activity)
//        })

        stopBtn.setOnClickListener {
            viewModel.processEvent(MeditationEvent.Stop)
        }

        if (BuildConfig.DEBUG) {
            skipBtn.visibility = View.VISIBLE
            skipBtn.setOnClickListener {
                viewModel.processEvent(MeditationEvent.Complete)
            }
        } else {
            skipBtn.visibility = View.GONE
        }

        playBtn.setOnClickListener {
            mediaPlayer?.start()
            applyPlayState()
        }

        pauseBtn.setOnClickListener {
            mediaPlayer?.pause()
            applyPauseState()
        }
    }

    override fun onApplyViewState(viewState: ViewState) {
        when (viewState) {
            is ActiveMeditationViewState -> {
                handleActiveMeditation(viewState)
            }
        }
    }

    private fun handleActiveMeditation(viewState: ActiveMeditationViewState) {
        val audioUri = ResByName.expansionFileUri(context!!, viewState.meditation.getSelectedAudio().path)

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(AudioAttributes.Builder().setContentType(CONTENT_TYPE_MUSIC).build())
            setDataSource(context!!, audioUri)
            setWakeMode(context, PowerManager.FULL_WAKE_LOCK)
            setOnPreparedListener(this@MeditationFragment)
            setOnCompletionListener(this@MeditationFragment)
        }

        mediaPlayer?.prepareAsync()
    }

    override fun onCompletion(mp: MediaPlayer) {
        viewModel.processEvent(MeditationEvent.Complete)
    }

    override fun onPrepared(mp: MediaPlayer) {
        mp.start()
        progressBar.max = mp.duration
        audioTimerDisposable?.dispose()
        audioTimerDisposable = audioTimerObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                progressBar.progress = mp.currentPosition
                timeTextView.text =
                        DurationFormatUtils.formatDuration((mp.duration - mp.currentPosition).toLong(), "mm:ss")

            }
        applyPlayState()
    }

    private fun applyPlayState() {
        stopBtn.visibility = View.GONE
        playBtn.visibility = View.GONE
        pauseBtn.visibility = View.VISIBLE
    }

    private fun applyPauseState() {
        stopBtn.visibility = View.VISIBLE
        playBtn.visibility = View.VISIBLE
        pauseBtn.visibility = View.GONE
    }

    override fun onDestroyView() {
        audioTimerDisposable?.dispose()
        mediaPlayer?.release()
        super.onDestroyView()

    }
}

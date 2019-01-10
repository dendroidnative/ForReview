package com.forreview.ui.meditation.main

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import com.forreview.R
import com.forreview.base.BaseActivity
import com.forreview.helper.GlideHelper
import com.forreview.helper.MeditationNavigation
import com.forreview.helper.ResByName
import com.forreview.model.NavigationResult
import com.forreview.model.ViewState
import com.mklimek.frameviedoview.FrameVideoViewListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_meditation.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

private const val ACTION_PAID = "com.forreview.ui.meditation.ACTION_PAID"

class MeditationActivity : BaseActivity() {

    private val viewModel by viewModel<MeditationMainViewModel>()
    private var navigationDisposable: Disposable? = null
//    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)

        subscribeToNavigationRoute()

        if (intent.action != ACTION_PAID) {
            subscribe(viewModel.meditationObservable)
            viewModel.processEvent(MeditationEvent.GetActiveMeditation)
        } else {
            subscribe(viewModel.paidMeditationObservable)
            viewModel.processEvent(MeditationEvent.GetPaidActiveMeditation)
        }
//        player = ExoPlayerFactory.newSimpleInstance(this)
//        player?.repeatMode = Player.REPEAT_MODE_ALL
//        player?.playWhenReady = true
//
//        playerView.requestFocus()
//        playerView.useController = false
//        playerView.player = player

//        videoView.visibility = View.INVISIBLE
    }

    private fun subscribeToNavigationRoute() {
        navigationDisposable = viewModel.routeObservable.observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Timber.e("meditation activity action $it")
                when (it) {
                    is NavigationResult.Stop -> {
                        MeditationNavigation.popToStages(this)
                    }
                    is NavigationResult.Complete -> {
                        dispose()
                        MeditationNavigation.popToStages(this)
                    }

                    is NavigationResult.GoToDurationSelection -> {
                        MeditationNavigation.fromStartMeditationToDurationSelection(this)
                    }

                    is NavigationResult.GoToPreMeditation -> {
                        MeditationNavigation.fromStartMeditationToPreMeditation(this, 0)
                    }
                    is NavigationResult.GoToPreMeditationSelf -> {
                        MeditationNavigation.preMeditationSelf(this, it.value)
                    }
                    is NavigationResult.GoToPreMeditationFromDuration -> {
                        MeditationNavigation.fromDurationSelectionToPreMeditation(this, 0)
                    }


                    is NavigationResult.GoToMeditation -> {
                        MeditationNavigation.fromStartMeditationToMeditation(this)
                    }
                    is NavigationResult.GoToMeditationFromDuration -> {
                        MeditationNavigation.fromDurationSelectionToMeditation(this)
                    }
                    is NavigationResult.GoToMeditationFromPreMeditation -> {
                        MeditationNavigation.fromPreMeditationToMeditation(this)
                    }



                    is NavigationResult.GoToWakeUpFromMeditation -> {
                        MeditationNavigation.fromMeditationToWakeup(this)
                    }
                    is NavigationResult.GoToPostMeditation -> {
                        MeditationNavigation.fromWakeupToPostMeditation(this, 0)
                    }
                    is NavigationResult.GoToPostMeditationSelf -> {
                        MeditationNavigation.postMeditationSelf(this, it.value)
                    }



                    is NavigationResult.GoNextMeditation -> {
                        viewModel.processEvent(MeditationEvent.GoNextMeditation)
                    }
                    is NavigationResult.SetCompleted -> {
                        viewModel.processEvent(MeditationEvent.SetCompleted(it.value))
                    }
                }
            }
    }


    override fun onApplyViewState(viewState: ViewState) {
        when (viewState) {
            is ActiveMeditationViewState -> {
                GlideHelper.loadBg(this, root, viewState.meditation.stage.staticBgPath)

                val videoUri = ResByName.expansionFileUri(this, viewState.meditation.stage.dynamicBgPath)
//                videoView.setVideoURI(videoUri)
//                videoView.setOnPreparedListener { player ->
//                    player.isLooping = true
//                }
//                videoView.start()

//                val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayer"))
//                val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
//                    .setExtractorsFactory(DefaultExtractorsFactory())
//                    .createMediaSource(videoUri)
//                player?.prepare(mediaSource)


                videoView.setup(videoUri)
                videoView.setFrameVideoViewListener(object : FrameVideoViewListener {
                    override fun mediaPlayerPrepared(mediaPlayer: MediaPlayer) {
                        mediaPlayer.start()
                    }

                    override fun mediaPlayerPrepareFailed(mediaPlayer: MediaPlayer, error: String) {}
                })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navigationDisposable?.dispose()
    }

    companion object {
        fun start(activity: Activity?) {
            activity ?: return
            activity.startActivity(Intent(activity, MeditationActivity::class.java))
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        fun startPaid(activity: Activity?) {
            activity ?: return
            activity.startActivity(Intent(activity, MeditationActivity::class.java).apply {
                action = ACTION_PAID
            })
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFinishing) {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}

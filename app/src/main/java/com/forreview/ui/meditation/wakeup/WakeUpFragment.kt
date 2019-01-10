package com.forreview.ui.meditation.wakeup

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.forreview.R
import com.forreview.base.BaseFragment
import com.forreview.helper.ResByName
import com.forreview.helper.SchedulerProvider
import com.forreview.ui.meditation.main.MeditationEvent
import com.forreview.ui.meditation.main.MeditationMainViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_wakeup.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

private const val ACTION_PAID = "com.forreview.ui.meditation.ACTION_PAID"

class WakeUpFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MeditationMainViewModel>()

    private val schedulers by inject<SchedulerProvider>()

    private var animationDisposable: Disposable? = null

    private val animationObservable = Observable.fromCallable {
        return@fromCallable mutableListOf<Int>().also { list ->
            for (i in 98 downTo 0) {
                val drawableRes = if (i < 10) {
                    ResByName.drawable(context!!, "eye0$i")
                } else {
                    ResByName.drawable(context!!, "eye$i")
                }
                list.add(drawableRes)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wakeup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.addObserver(viewModel)

        subscribe(viewModel.meditationObservable)

        if (activity?.intent?.action != ACTION_PAID) {
            subscribe(viewModel.meditationObservable)
        } else {
            subscribe(viewModel.paidMeditationObservable)
        }

        eyeImageView.setOnClickListener {
            viewModel.processEvent(MeditationEvent.Next)
        }

//        viewModel.onNextAction.observe(viewLifecycleOwner, Observer {
//            MeditationNavigation.fromWakeupToPostMeditation(activity, it)
//        })
//
//        viewModel.onCompleteAction.observe(viewLifecycleOwner, Observer {
//            dispose()
//            MeditationNavigation.popToStages(activity)
//        })


        animationDisposable = animationObservable
            .flatMap {
                Observable.zip(
                    Observable.fromIterable(it),
                    Observable.interval(50, TimeUnit.MILLISECONDS),
                    BiFunction<Int, Long, Drawable> { resId, _ ->
                        return@BiFunction ContextCompat.getDrawable(context!!, resId)!!
                    })
            }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({
                eyeImageView.background = it
            }, {
                viewModel.processEvent(MeditationEvent.Next)
            }, {
                viewModel.processEvent(MeditationEvent.Next)
            })


//        val frameAnimation = eyeImageView.background as AnimationDrawable
//        frameAnimation.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        animationDisposable?.dispose()
    }
}

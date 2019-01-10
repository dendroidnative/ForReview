package com.forreview.ui.meditation.duration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.forreview.R
import com.forreview.base.BaseFragment
import com.forreview.model.ViewState
import com.forreview.ui.meditation.main.ActiveMeditationViewState
import com.forreview.ui.meditation.main.MeditationEvent
import com.forreview.ui.meditation.main.MeditationMainViewModel
import kotlinx.android.synthetic.main.duration_selector_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

private const val ACTION_PAID = "com.forreview.ui.meditation.ACTION_PAID"

class DurationSelectorFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MeditationMainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.duration_selector_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)

        if (activity?.intent?.action != ACTION_PAID) {
            subscribe(viewModel.meditationObservable)
        } else {
            subscribe(viewModel.paidMeditationObservable)
        }

//        viewModel.goToMeditationAction.observe(viewLifecycleOwner, Observer {
//            MeditationNavigation.fromDurationSelectionToMeditation(activity)
//        })
//
//        viewModel.goToPreMeditationAction.observe(viewLifecycleOwner, Observer {
//            MeditationNavigation.fromDurationSelectionToPreMeditation(activity, it)
//        })
    }

    override fun onApplyViewState(viewState: ViewState) {
        when (viewState) {
            is ActiveMeditationViewState -> {
                handleActiveMeditation(viewState)
            }
        }
    }

    private fun handleActiveMeditation(viewState: ActiveMeditationViewState) {
        Timber.d("handleActiveMeditation ${viewState.meditation.audioList.size}")
        viewState.meditation.audioList.forEachIndexed { index, audio ->
            val view = LayoutInflater.from(context).inflate(R.layout.item_meditation_duration, null, false)
            val textView = view.findViewById<TextView>(R.id.textView)
            textView.text = "${audio.getDurationMin()}"
            textView.setOnClickListener {
                viewModel.processEvent(MeditationEvent.SelectDuration(audio))
            }
            selectorView.addView(view)
        }
    }
}
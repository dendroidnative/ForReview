package com.forreview.ui.meditation.explanation.premeditation

import android.os.Bundle
import com.forreview.model.ViewState
import com.forreview.ui.meditation.explanation.ExplanationFragment
import com.forreview.ui.meditation.main.ActiveMeditationViewState
import com.forreview.ui.meditation.main.MeditationEvent
import com.forreview.ui.meditation.main.MeditationMainViewModel
import kotlinx.android.synthetic.main.fragment_explanation.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


private const val ACTION_PAID = "com.forreview.ui.meditation.ACTION_PAID"
class PreMeditationFragment : ExplanationFragment() {

    private val viewModel by sharedViewModel<MeditationMainViewModel>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewLifecycleOwner.lifecycle.addObserver(viewModel)

        if (activity?.intent?.action != ACTION_PAID) {
            subscribe(viewModel.meditationObservable)
        } else {
            subscribe(viewModel.paidMeditationObservable)
        }

        arguments?.getInt(MeditationMainViewModel.KEY_INFO_POSITION, 0)?.also {
            viewModel.prePosition = it
        }

        nextBtn.setOnClickListener {
            viewModel.processEvent(MeditationEvent.NextPreCard(viewModel.prePosition))
        }

//        viewModel.goToPreMeditationAction.observe(viewLifecycleOwner, Observer {
//            MeditationNavigation.preMeditationSelf(activity, it)
//        })
//
//        viewModel.goToMeditationAction.observe(viewLifecycleOwner, Observer {
//            MeditationNavigation.fromPreMeditationToMeditation(activity)
//        })
    }

    override fun onApplyViewState(viewState: ViewState) {
        when(viewState) {
            is ActiveMeditationViewState -> {
                handleActiveMeditation(viewState)
            }
        }
    }

    private fun handleActiveMeditation(viewState: ActiveMeditationViewState) {
        val explanationList = viewState.meditation.preMeditateList[viewModel.prePosition]
        applyExplanation(explanationList)
        applyMeditation(viewState.meditation)
    }
}

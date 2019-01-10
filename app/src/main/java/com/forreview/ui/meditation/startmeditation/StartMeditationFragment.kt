package com.forreview.ui.meditation.startmeditation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import com.forreview.R
import com.forreview.base.BaseFragment
import com.forreview.helper.ResByName
import com.forreview.model.ViewState
import com.forreview.ui.meditation.main.ActiveMeditationViewState
import com.forreview.ui.meditation.main.MeditationEvent
import com.forreview.ui.meditation.main.MeditationMainViewModel
import kotlinx.android.synthetic.main.fragment_start_meditation.*
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val ACTION_PAID = "com.forreview.ui.meditation.ACTION_PAID"

class StartMeditationFragment : BaseFragment() {

    private val viewModel by viewModel<MeditationMainViewModel>()

    private lateinit var outAnimatorSet: AnimatorSet
    private lateinit var inAnimatorSet: AnimatorSet

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_start_meditation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)

        if (activity?.intent?.action != ACTION_PAID) {
            subscribe(viewModel.meditationObservable)
        } else {
            subscribe(viewModel.paidMeditationObservable)
        }

        initAnimatorSet()

//        viewModel.goToMeditationAction.observe(viewLifecycleOwner, Observer {
//            MeditationNavigation.fromStartMeditationToMeditation(activity)
//        })

//        viewModel.goToDurationSelectionAction.observe(viewLifecycleOwner, Observer {
//            MeditationNavigation.fromStartMeditationToDurationSelection(activity)
//        })

//        viewModel.goToPreMeditationAction.observe(viewLifecycleOwner, Observer {
//            MeditationNavigation.fromStartMeditationToPreMeditation(activity, it)
//        })

        startIcon0.setOnClickListener {
            outAnimatorSet.pause()
            inAnimatorSet.start()
        }

        startBtn.animate().alpha(1f).startDelay = 5000
    }


    override fun onApplyViewState(viewState: ViewState) {
        when (viewState) {
            is ActiveMeditationViewState -> {
                handleActiveMeditation(viewState)
            }
        }
    }

    private fun handleActiveMeditation(viewState: ActiveMeditationViewState) {
        val resId = ResByName.drawable(context!!, viewState.meditation.stage.startMeditationIconPath)
        startIcon0.setImageResource(resId)
        startIcon1.setImageResource(resId)
        startIcon2.setImageResource(resId)
        startIcon3.setImageResource(resId)

        outAnimatorSet.start()
    }


    private fun initAnimatorSet() {
        outAnimatorSet = AnimatorSet()
        outAnimatorSet.playTogether(
            setOutAnimation(startIcon1, 0),
            setOutAnimation(startIcon2, 600),
            setOutAnimation(startIcon3, 1200)
        )
        outAnimatorSet.startDelay = 0
        outAnimatorSet.addListener({
            it.startDelay = 1000
            it.start()
        })

        inAnimatorSet = AnimatorSet()
        inAnimatorSet.playTogether(
            setInAnimation(startIcon1, 200),
            setInAnimation(startIcon2, 100),
            setInAnimation(startIcon3, 0)
        )
        inAnimatorSet.startDelay = 0
        inAnimatorSet.addListener({
            viewModel.processEvent(MeditationEvent.GoMeditation)
        })
    }

    private fun setOutAnimation(view: View, delay: Long): Animator {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 3f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 3f)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)

        return AnimatorSet().apply {
            duration = 3000
            startDelay = delay
            playTogether(scaleX, scaleY, alpha)
        }
    }

    private fun setInAnimation(view: View, delay: Long): Animator {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f)

        return AnimatorSet().apply {
            duration = 800
            startDelay = delay
            playTogether(scaleX, scaleY, alpha)
        }
    }
}

package com.forreview.ui.menu.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.forreview.R
import com.forreview.base.BaseFragment
import com.forreview.helper.GlideHelper
import com.forreview.model.Stat
import com.forreview.model.ViewState
import kotlinx.android.synthetic.main.fragment_statistics.*
import org.koin.androidx.viewmodel.ext.android.viewModel

data class StatViewState(
    val stat: Stat
): ViewState

class StatisticsFragment : BaseFragment() {

    private val viewModel by viewModel<StatisticsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        root.visibility = View.GONE
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        subscribe(viewModel.statObservable)

    }

    override fun onApplyViewState(viewState: ViewState) {
        when(viewState) {
            is StatViewState -> {
                numberTextView.text = "${viewState.stat.totalMeditations}"
                streakTextView.text = "${viewState.stat.longestStreak}"
                vibeTextView.text = getString(R.string.fragment_stat_current_vibe, viewState.stat.vibe)
                timeTextView.text = getString(R.string.fragment_stat_total_time_meditating, viewState.stat.getTotalTimeString())

                GlideHelper.load(context, numberImageView, viewState.stat.achievementIconResId)
                GlideHelper.load(context, streakImageView, viewState.stat.streakIconResId)

                root.alpha = 0f
                root.visibility = View.VISIBLE
                root.animate().alpha(1f)
            }
        }
    }

    companion object {
        const val TAG = "StatisticsFragment"
    }
}

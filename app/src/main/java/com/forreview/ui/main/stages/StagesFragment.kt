package com.forreview.ui.main.stages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.forreview.BuildConfig
import com.forreview.R
import com.forreview.base.BaseFragment
import com.forreview.model.ActiveStageViewState
import com.forreview.model.Stage
import com.forreview.model.ViewState
import com.forreview.ui.meditation.main.MeditationActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_stages.*
import kotlinx.android.synthetic.main.item_stage.*
import org.koin.androidx.viewmodel.ext.android.viewModel

data class MeditationAvailableViewState(
    val isAvailable: Boolean
): ViewState

class StagesFragment : BaseFragment() {

    private val viewModel by viewModel<StagesViewModel>()

    private val stagesAdapter = StagesAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        upBtn.setOnClickListener {
            val firstVisiblePosition = (stagesRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            if (firstVisiblePosition > 0) {
                stagesRecyclerView.smoothScrollToPosition(firstVisiblePosition - 1)
            }
        }

        downBtn.setOnClickListener {
            val lastVisiblePosition = (stagesRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            if (lastVisiblePosition < stagesAdapter.itemCount - 1) {
                stagesRecyclerView.smoothScrollToPosition(lastVisiblePosition + 1)
            }
        }

        if (BuildConfig.DEBUG) {
            startBtn.setOnLongClickListener {
                viewModel.processEvent(StageListEvent.StartMeditation)
                return@setOnLongClickListener true
            }
        }

        stagesRecyclerView.adapter = stagesAdapter
        stagesRecyclerView.layoutManager = LinearLayoutManager(context)

        subscribe(viewModel.stageObservable)
        subscribe(viewModel.meditationObservable)

        viewModel.onStartMeditationAction.observe(viewLifecycleOwner, Observer {
//            MainNavigation.fromStagesToStartMeditation(activity)
            MeditationActivity.start(activity)
        })
    }

    override fun onApplyViewState(viewState: ViewState) {
        when(viewState) {
            is StageListViewState -> {
                handleStateList(viewState)
            }
            is ActiveStageViewState -> {
                handleActiveState(viewState)
            }
            is MeditationAvailableViewState -> {
                if (viewState.isAvailable) {
                    startBtn.text = getString(R.string.fragment_stages_start_btn_begin)
                    startBtn.setOnClickListener {
                        viewModel.processEvent(StageListEvent.StartMeditation)
                    }
                } else {
                    startBtn.text = getString(R.string.fragment_stages_start_btn_see_you_tomorrow)
                    startBtn.setOnClickListener {

                    }
                }
            }
        }
    }

    private fun handleStateList(viewState: StageListViewState) {
        stagesAdapter.swapData(viewState.stageList)
    }

    private fun handleActiveState(viewState: ActiveStageViewState) {
        val stage = viewState.stage
        stageProgressView.setProgress(stage.getCurrentDayNumber(), stage.daysCount)

        val pos = stagesAdapter.findPosition(stage)
        stagesRecyclerView.smoothScrollToPosition(pos)
    }

    class StagesAdapter: RecyclerView.Adapter<StageViewHolder>() {

        private val stageList = mutableListOf<Stage>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StageViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stage, parent, false)
            return StageViewHolder(view)
        }

        override fun getItemCount() = stageList.size

        override fun onBindViewHolder(holder: StageViewHolder, position: Int) {
            holder.onBindViewHolder(stage = stageList[position])
        }

        fun swapData(stageList: List<Stage>) {
            this.stageList.clear()
            this.stageList.addAll(stageList)
            notifyDataSetChanged()
        }

        fun findPosition(stage: Stage): Int {
            stageList.forEachIndexed { index, item ->
                if (stage.id == item.id) {
                    return index
                }
            }
            return 0
        }
    }

    class StageViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun onBindViewHolder(stage: Stage) {
            titleTextView.text = stage.title
            descriptionTextView.text = stage.subtitle

            when(stage.getStatus()) {
                Stage.Status.BLOCKED -> {
                    titleTextView.alpha = 0.5f
                    descriptionTextView.alpha = 0.5f
                }
                Stage.Status.COMPLETED -> {
                    titleTextView.alpha = 1f
                    descriptionTextView.alpha = 1f
                }
                Stage.Status.ACTIVE -> {
                    titleTextView.alpha = 1f
                    descriptionTextView.alpha = 1f
                }
            }
        }
    }

    companion object {
        const val TAG = "StagesFragment"
    }
}

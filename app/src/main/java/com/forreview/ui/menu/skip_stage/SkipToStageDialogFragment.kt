package com.forreview.ui.menu.skip_stage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.forreview.R
import com.forreview.base.BaseBottomDialogFragment
import com.forreview.helper.GlideHelper.loadBg
import com.forreview.model.ActiveStageViewState
import com.forreview.model.Meditation
import com.forreview.model.ViewState
import kotlinx.android.synthetic.main.fragment_skip_to_stage.*
import kotlinx.android.synthetic.main.item_fragment_skip_to_stage.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

data class ActiveMeditationViewState(
    val isLoading: Boolean,
    val meditation: Meditation,
    val error: Throwable?
): ViewState

data class MeditationListViewState(
    val isLoading: Boolean,
    val meditationList: List<Meditation>,
    val error: Throwable?
): ViewState

class SkipToStageDialogFragment : BaseBottomDialogFragment() {

    private val viewModel by viewModel<SkipToStageViewModel>()

    private val adapter = ItemAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_skip_to_stage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = adapter

        viewLifecycleOwner.lifecycle.addObserver(viewModel)

        subscribe(viewModel.meditationObservable)
        subscribe(viewModel.stageObservable)
    }

    override fun onApplyViewState(viewState: ViewState) {
        when(viewState) {
            is ActiveMeditationViewState -> {

            }
            is MeditationListViewState -> {
                adapter.swapData(viewState.meditationList)
            }
            is ActiveStageViewState -> {
                loadBg(context, list, viewState.stage.staticBgPath)
            }
        }
    }

    private inner class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

        private val meditationList = mutableListOf<Meditation>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindViewHolder(meditationList[position])
        }

        override fun getItemCount(): Int {
            return meditationList.size
        }

        fun swapData(meditationList: List<Meditation>) {
            this.meditationList.clear()
            this.meditationList.addAll(meditationList)
            notifyDataSetChanged()
        }

        private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.item_fragment_skip_to_stage, parent, false)) {

            internal val text: TextView = itemView.text

            init {

                text.setOnClickListener {
                    Timber.d("click")
                    viewModel.processEvent(
                        MeditationListEvent.JumpToMeditation(
                            meditationList[adapterPosition]
                        )
                    )
                }
            }

            fun bindViewHolder(meditation: Meditation) {

                text.text = "${meditation.stage.title} - Day ${meditation.dayNumber + 1}"
                if (meditation.isActive()) {
                    text.alpha = 1f
                } else {
                    text.alpha = 0.7f
                }
            }
        }
    }

    companion object {

        const val TAG = "SkipToStageDialogFragment"

        fun show(fragmentManager: FragmentManager) {
            SkipToStageDialogFragment()
                .show(fragmentManager, TAG)
        }
    }
}

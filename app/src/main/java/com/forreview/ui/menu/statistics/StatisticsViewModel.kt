package com.forreview.ui.menu.statistics

import com.forreview.base.BaseViewModel
import com.forreview.datamanager.DataManager
import com.forreview.helper.CoroutineExecutor
import com.forreview.model.Event
import com.forreview.model.StatResult
import com.forreview.model.ViewState
import com.forreview.model.*
import io.reactivex.Observable

sealed class StatEvent: Event {
    object GetStat: StatEvent()
}

abstract class StatisticsViewModel: BaseViewModel() {
    abstract val statObservable: Observable<ViewState>
    abstract fun processEvent(event: StatEvent)
}

class StatisticsViewModelImpl(
    private val dataManager: DataManager,
    private val executor: CoroutineExecutor
): StatisticsViewModel() {

    override val statObservable: Observable<ViewState>
        get() = dataManager.statObservable
            .map(this::viewStateFromResult)

    private fun viewStateFromResult(result: StatResult): ViewState {
        return when(result) {
            is StatResult.LoadStat -> {
                when(result) {
                    is StatResult.LoadStat.Success -> {
                        StatViewState(result.stat)
                    }
                }
            }
        }
    }

    override fun processEvent(event: StatEvent) {

    }
}
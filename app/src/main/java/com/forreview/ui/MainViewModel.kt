package com.forreview.ui

import com.forreview.base.BaseViewModel
import com.forreview.datamanager.DataManager
import com.forreview.helper.CoroutineExecutor
import com.forreview.model.ActiveStageViewState
import com.forreview.model.StageAction
import com.forreview.model.StageResult
import com.forreview.model.ViewState
import com.forreview.utils.launchSilent
import io.reactivex.Observable

abstract class MainViewModel: BaseViewModel() {
    abstract val stageObservable: Observable<ViewState>
    abstract fun processEvent(event: MainEvent)
}

class MainViewModelImpl(
    private val dataManager: DataManager,
    private val executor: CoroutineExecutor
): MainViewModel() {

    override fun onCreateView() {
        processEvent(MainEvent.FindActiveStage)
    }

    override val stageObservable: Observable<ViewState>
        get() = dataManager.activeStageObservable
            .map(this::viewStateFromResult)

    private fun viewStateFromResult(result: StageResult): ViewState {
        return when(result) {
            is StageResult.LoadStageList -> {
                TODO("not implemented")
            }
            is StageResult.ActiveStage -> {
                when(result) {
                    is StageResult.ActiveStage.Success -> {
                        ActiveStageViewState(false, result.stage, null)
                    }
                    is StageResult.ActiveStage.InFlight -> {
                        TODO("not implemented")
                    }
                    is StageResult.ActiveStage.Failure -> {
                        TODO("not implemented")
                    }
                }
            }
            is StageResult.LoadStage -> {
                TODO("not implemented")
            }
        }
    }

    override fun processEvent(event: MainEvent) = launchSilent(executor.ui()) {
        dataManager.processAction(
            when (event) {
                is MainEvent.FindActiveStage -> {
                    StageAction.FindActiveStage(false)
                }
            }
        )
    }
}
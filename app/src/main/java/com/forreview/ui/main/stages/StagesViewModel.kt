package com.forreview.ui.main.stages

import com.forreview.SingleLiveEvent
import com.forreview.base.BaseViewModel
import com.forreview.datamanager.DataManager
import com.forreview.helper.CoroutineExecutor
import com.forreview.model.*
import com.forreview.model.*
import com.forreview.utils.launchSilent
import io.reactivex.Observable
import timber.log.Timber

abstract class StagesViewModel : BaseViewModel() {
    abstract val stageObservable: Observable<ViewState>
    abstract val meditationObservable: Observable<ViewState>

    abstract fun processEvent(event: StageListEvent)

    abstract val onStartMeditationAction: SingleLiveEvent<Unit>
}

class StagesViewModelImpl(
    private val dataManager: DataManager,
    private val executor: CoroutineExecutor
) : StagesViewModel() {

    override val onStartMeditationAction = SingleLiveEvent<Unit>()

    override val stageObservable: Observable<ViewState>
        get() = Observable.merge(dataManager.stageListObservable, dataManager.activeStageObservable)
            .map(this::viewStateFromResult)

    override val meditationObservable: Observable<ViewState>
        get() = dataManager.meditationAvailableObservable
            .map(this::viewStateFromResult)

    private fun viewStateFromResult(result: StageResult): ViewState {
        Timber.tag("MeditationRepository").d("viewStateFromResult: $result")
        return when (result) {
            is StageResult.LoadStageList -> {
                when (result) {
                    is StageResult.LoadStageList.Success -> {
                        StageListViewState(false, result.stageList.filter {
                            it.getType() == Stage.Type.FREE
                        }, null)
                    }
                    is StageResult.LoadStageList.Failure -> {
                        TODO("not implemented")
                    }
                    is StageResult.LoadStageList.InFlight -> {
                        TODO("not implemented")
                    }
                }
            }

            is StageResult.ActiveStage -> {
                when (result) {
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
                when (result) {
                    is StageResult.LoadStage.Success -> {
                        ActiveStageViewState(false, result.stage, null)
                    }
                    is StageResult.LoadStage.InFlight -> {
                        TODO("not implemented")
                    }
                    is StageResult.LoadStage.Failure -> {
                        TODO("not implemented")
                    }
                }
            }
        }
    }

    private fun viewStateFromResult(result: MeditationsResult): ViewState {
        return when (result) {
            is MeditationsResult.MeditationList -> {
                TODO("not implemented")
            }
            is MeditationsResult.ActiveMeditation -> {
                TODO("not implemented")
            }
            is MeditationsResult.LoadMeditationResult -> {
                TODO("not implemented")
            }
            is MeditationsResult.IsAvailableMeditation.Success -> {
                MeditationAvailableViewState(result.isAvailable)
            }
            is MeditationsResult.ActivePaidMeditation -> {
                TODO("not implemented")
            }
        }
    }

    override fun onCreateView() {
        processEvent(StageListEvent.Initial)
        processEvent(StageListEvent.FindActiveStage)
        processEvent(StageListEvent.IsMeditationAvailable)
    }

    override fun processEvent(event: StageListEvent) =
        launchSilent(executor.ui()) {
            Timber.tag("MeditationRepository").d("processEvent: $event")
            when (event) {
                is StageListEvent.Initial -> {
                    dataManager.processAction(StageAction.LoadStageList(false))
                }
                is StageListEvent.Refresh -> {
                    dataManager.processAction(StageAction.LoadStageList(true))
                }
                is StageListEvent.FindActiveStage -> {
                    dataManager.processAction(StageAction.FindActiveStage(false))
                }
                is StageListEvent.GoNextMeditation -> {
                    dataManager.processAction(MeditationAction.GoNextMeditation)
                }
                is StageListEvent.StartMeditation -> {
                    onStartMeditationAction.postValue(Unit)
                }
                is StageListEvent.IsMeditationAvailable -> {
                    dataManager.processAction(MeditationAction.IsMeditationAvailable(false))
                }
            }
        }
}
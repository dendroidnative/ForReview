package com.forreview.ui.menu.skip_stage

import com.forreview.base.BaseViewModel
import com.forreview.datamanager.DataManager
import com.forreview.helper.CoroutineExecutor
import com.forreview.model.*
import com.forreview.model.*
import com.forreview.utils.launchSilent
import io.reactivex.Observable

sealed class MeditationListEvent: Event {

    object Initial: MeditationListEvent()

    object FindActiveMeditation: MeditationListEvent()

    object FindActiveStage: MeditationListEvent()

    data class JumpToMeditation(val meditation: Meditation): MeditationListEvent()
}

abstract class SkipToStageViewModel: BaseViewModel() {
    abstract val meditationObservable: Observable<ViewState>
    abstract val stageObservable: Observable<ViewState>

    abstract val testObservable: Observable<ViewState>

    abstract fun processEvent(event: MeditationListEvent)
}

class SkipToStageViewModelImpl(
    private val dataManager: DataManager,
    private val executor: CoroutineExecutor
): SkipToStageViewModel() {

    override val meditationObservable: Observable<ViewState>
        get() = Observable.merge(dataManager.meditationListObservable, dataManager.activeMeditationObservable)
            .map(this::viewStateFromResult)

    override val stageObservable: Observable<ViewState>
        get() = dataManager.activeStageObservable
            .map(this::viewStateFromResult)

    override val testObservable: Observable<ViewState>
        get() = Observable.merge(dataManager.meditationListObservable, dataManager.activeMeditationObservable)
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

    private fun viewStateFromResult(result: MeditationsResult): ViewState {
        return when(result) {
            is MeditationsResult.MeditationList -> {
                when(result) {
                    is MeditationsResult.MeditationList.Success -> {
                        MeditationListViewState(false, result.meditationList, null)
                    }
                    is MeditationsResult.MeditationList.Failure -> {
                        TODO("not implemented")
                    }
                    is MeditationsResult.MeditationList.InFlight -> {
                        TODO("not implemented")
                    }
                }
            }
            is MeditationsResult.ActiveMeditation -> {
                when(result) {
                    is MeditationsResult.ActiveMeditation.Success -> {
                        ActiveMeditationViewState(false, result.meditation, null)
                    }
                    is MeditationsResult.ActiveMeditation.Failure -> {
                        TODO("not implemented")
                    }
                    is MeditationsResult.ActiveMeditation.InFlight -> {
                        TODO("not implemented")
                    }
                }
            }
            is MeditationsResult.LoadMeditationResult -> {
                TODO("not implemented")
            }
            is MeditationsResult.IsAvailableMeditation.Success -> {
                TODO("not implemented")
            }
            is MeditationsResult.ActivePaidMeditation -> {
                TODO("not implemented")
            }
        }
    }

    override fun processEvent(event: MeditationListEvent) =
        launchSilent(executor.ui()) {
            dataManager.processAction(
                when (event) {
                    is MeditationListEvent.Initial -> {
                        MeditationAction.LoadMeditationList(false)
                    }
                    is MeditationListEvent.JumpToMeditation -> {
                        MeditationAction.JumpToMeditation(event.meditation)
                    }
                    is MeditationListEvent.FindActiveMeditation -> {
                        MeditationAction.LoadActiveMeditation(false)
                    }
                    is MeditationListEvent.FindActiveStage -> {
                        StageAction.FindActiveStage(false)
                    }
                }
            )
        }

    override fun onCreateView() {
        processEvent(MeditationListEvent.Initial)
        processEvent(MeditationListEvent.FindActiveMeditation)
        processEvent(MeditationListEvent.FindActiveStage)
    }

}
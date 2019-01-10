package com.forreview.ui.meditation.main

import com.forreview.base.BaseViewModel
import com.forreview.datamanager.DataManager
import com.forreview.helper.CoroutineExecutor
import com.forreview.model.*
import com.forreview.model.*
import com.forreview.utils.launchSilent
import io.reactivex.Observable

sealed class MeditationEvent : Event {
    object GetActiveMeditation : MeditationEvent()
    object GetPaidActiveMeditation : MeditationEvent()
    object Stop : MeditationEvent()
    object Complete : MeditationEvent()
    object GoMeditation : MeditationEvent()
    object Next : MeditationEvent()
    data class NextPostCard(val position: Int) : MeditationEvent()
    data class NextPreCard(val position: Int) : MeditationEvent()
    object GoNextMeditation : MeditationEvent()
    data class SetCompleted(val value: Meditation) : MeditationEvent()
    data class SelectDuration(val audio: Meditation.Audio) : MeditationEvent()
}

data class ActiveMeditationViewState(
    val isLoading: Boolean,
    val meditation: Meditation,
    val prePosition: Int,
    val postPosition: Int,
    val error: Throwable?
) : ViewState

abstract class MeditationMainViewModel : BaseViewModel() {
    abstract val meditationObservable: Observable<ViewState>
    abstract val paidMeditationObservable: Observable<ViewState>
    abstract val routeObservable: Observable<NavigationResult>
    abstract var prePosition: Int
    abstract var postPosition: Int
    abstract fun processEvent(event: MeditationEvent)

    companion object {
        const val KEY_INFO_POSITION = "KEY_POST_INFO_POSITION"
    }
}

class MeditationMainViewModelImpl(
    private val dataManager: DataManager,
    private val executor: CoroutineExecutor
) : MeditationMainViewModel() {
    override val routeObservable: Observable<NavigationResult>
        get() = dataManager.navigationRouteObservable

    override val meditationObservable: Observable<ViewState>
        get() = dataManager.activeMeditationObservable
            .map(this::viewStateFromResult)

    override val paidMeditationObservable: Observable<ViewState>
        get() = dataManager.activePaidMeditationObservable
            .map(this::viewStateFromResult)

    override var prePosition = 0
    override var postPosition = 0

    private lateinit var activeMeditation: Meditation

    private fun viewStateFromResult(result: MeditationsResult): ViewState {
        return when (result) {
            is MeditationsResult.MeditationList -> {
                TODO("not implemented")
            }
            is MeditationsResult.ActiveMeditation -> {
                when (result) {
                    is MeditationsResult.ActiveMeditation.Success -> {
                        activeMeditation = result.meditation
                        ActiveMeditationViewState(
                            false,
                            result.meditation,
                            prePosition,
                            postPosition,
                            null
                        )
                    }
                    is MeditationsResult.ActiveMeditation.Failure -> {
                        TODO("not implemented")
                    }
                    is MeditationsResult.ActiveMeditation.InFlight -> {
                        TODO("not implemented")
                    }
                }
            }
            is MeditationsResult.ActivePaidMeditation -> {
                when (result) {
                    is MeditationsResult.ActivePaidMeditation.Success -> {
                        activeMeditation = result.meditation
                        ActiveMeditationViewState(
                            false,
                            result.meditation,
                            prePosition,
                            postPosition,
                            null
                        )
                    }
                    is MeditationsResult.ActivePaidMeditation.Failure -> {
                        TODO("not implemented")
                    }
                    is MeditationsResult.ActivePaidMeditation.InFlight -> {
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

        }
    }

    override fun processEvent(event: MeditationEvent) =
        launchSilent(executor.ui()) {
            when (event) {
                is MeditationEvent.GetActiveMeditation -> {
                    dataManager.processAction(MeditationAction.LoadActiveMeditation(false))
                }
                is MeditationEvent.GetPaidActiveMeditation -> {
                    dataManager.processAction(MeditationAction.LoadPaidActiveMeditation(false))
                }
                is MeditationEvent.Stop -> {
                    dataManager.processAction(MeditationAction.Stop)
                }
                is MeditationEvent.Complete -> {
                    dataManager.processAction(MeditationAction.Complete)
                }
                is MeditationEvent.GoMeditation -> {
                    dataManager.processAction(MeditationAction.GoMeditation)
                }
                is MeditationEvent.Next -> {
                    dataManager.processAction(MeditationAction.Next)
                }
                is MeditationEvent.SelectDuration -> {
                    dataManager.processAction(MeditationAction.SelectDuration(event.audio))
                }
                is MeditationEvent.NextPostCard -> {
                    dataManager.processAction(MeditationAction.NextPostCard(event.position))
                }
                is MeditationEvent.NextPreCard -> {
                    dataManager.processAction(MeditationAction.NextPreCard(event.position))
                }


                is MeditationEvent.GoNextMeditation -> {
                    dataManager.processAction(MeditationAction.GoNextMeditation)
                }
                is MeditationEvent.SetCompleted -> {
                    dataManager.processAction(MeditationAction.SetCompleted(event.value))
                }
            }
        }
}
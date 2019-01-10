package com.forreview.helper

import com.forreview.model.Event
import com.forreview.model.Meditation
import com.forreview.model.NavigationResult
import com.forreview.model.Stage
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

sealed class MeditationEvent : Event {
    object Stop : MeditationEvent()
    object Complete : MeditationEvent()
    object GoMeditation : MeditationEvent()
    object FromWakeUpToPostMeditation : MeditationEvent()
    data class NextPostCard(val position: Int) : MeditationEvent()
    data class NextPreCard(val position: Int) : MeditationEvent()
    data class SelectDuration(val audio: Meditation.Audio) : MeditationEvent()
}

interface MeditationNavigationHelper {
    val navigationActionObservable: Observable<NavigationResult>
    var activeMeditation: Meditation
    var preCard: Int
    var postCard: Int
    fun routeEvent(event: Event)
}

class MeditationNavigationHelperImpl : MeditationNavigationHelper {
    override lateinit var activeMeditation: Meditation
    override val navigationActionObservable = PublishSubject.create<NavigationResult>()
    override var preCard = 0
    override var postCard = 0

    override fun routeEvent(event: Event) {
        when (event) {
            is MeditationEvent.Stop -> {
                navigationActionObservable.onNext(NavigationResult.Stop)
            }
            is MeditationEvent.Complete -> {
                navigationActionObservable.onNext(NavigationResult.GoToWakeUpFromMeditation)
            }
            is MeditationEvent.GoMeditation -> {
                if (::activeMeditation.isInitialized) {
                    when {
                        activeMeditation.audioList.size > 1 -> {
                            navigationActionObservable.onNext(NavigationResult.GoToDurationSelection)
                        }
                        activeMeditation.preMeditateList.isNotEmpty() -> {
                            navigationActionObservable.onNext(NavigationResult.GoToPreMeditation(0))
                        }
                        else -> {
                            navigationActionObservable.onNext(NavigationResult.GoToMeditation)
                        }
                    }
                }
            }
            is MeditationEvent.FromWakeUpToPostMeditation -> {
                if (::activeMeditation.isInitialized) {
                    if (activeMeditation.postMeditateList.isNotEmpty()) {
                        navigationActionObservable.onNext(NavigationResult.GoToPostMeditation(0))
                    } else {
                        navigationActionObservable.onNext(NavigationResult.Complete)
                        if (activeMeditation.stage.getType() == Stage.Type.FREE) {
                            navigationActionObservable.onNext(NavigationResult.GoNextMeditation)
                        } else {
                            navigationActionObservable.onNext(NavigationResult.SetCompleted(activeMeditation))
                        }
                    }
                }
            }
            is MeditationEvent.SelectDuration -> {
                if (::activeMeditation.isInitialized) {
                    activeMeditation.setSelectedAudio(event.audio)
                    if (activeMeditation.preMeditateList.isNotEmpty()) {
                        navigationActionObservable.onNext(NavigationResult.GoToPreMeditationFromDuration(0))
                    } else {
                        navigationActionObservable.onNext(NavigationResult.GoToMeditationFromDuration)
                    }
                }
            }
            is MeditationEvent.NextPostCard -> {
                if (::activeMeditation.isInitialized) {
                    postCard = event.position
                    if ((postCard + 1) < activeMeditation.postMeditateList.size) {
                        navigationActionObservable.onNext(NavigationResult.GoToPostMeditationSelf(postCard + 1))
                    } else {
                        navigationActionObservable.onNext(NavigationResult.Complete)
                        if (activeMeditation.stage.getType() == Stage.Type.FREE) {
                            navigationActionObservable.onNext(NavigationResult.GoNextMeditation)
                        } else {
                            navigationActionObservable.onNext(NavigationResult.SetCompleted(activeMeditation))
                        }
                    }
                }
            }
            is MeditationEvent.NextPreCard -> {
                if (::activeMeditation.isInitialized) {
                    preCard = event.position
                    if ((preCard + 1) < activeMeditation.preMeditateList.size) {
                        navigationActionObservable.onNext(NavigationResult.GoToPreMeditationSelf(preCard + 1))
                    } else {
                        navigationActionObservable.onNext(NavigationResult.GoToMeditationFromPreMeditation)
                    }
                }
            }
        }
    }
}
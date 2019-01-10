package com.forreview.model

interface Action

sealed class MeditationAction : Action {
    data class LoadMeditationList(val isForce: Boolean) : MeditationAction()
    data class LoadPaidMeditationList(val isForce: Boolean) : MeditationAction()
    data class LoadMeditation(val id: String) : MeditationAction()
    data class UpdateMeditationAction(val meditation: Meditation) : MeditationAction()
    object GoNextMeditation : MeditationAction()
    data class LoadActiveMeditation(val isForce: Boolean) : MeditationAction()
    data class LoadPaidActiveMeditation(val isForce: Boolean) : MeditationAction()
    data class JumpToMeditation(val meditation: Meditation) : MeditationAction()
    data class IsMeditationAvailable(val isForce: Boolean) : MeditationAction()
    data class SetCompleted(val meditation: Meditation) : MeditationAction()
    object Stop : MeditationAction()
    object Complete : MeditationAction()
    object GoMeditation : MeditationAction()
    object Next : MeditationAction()
    data class NextPostCard(val position: Int) : MeditationAction()
    data class NextPreCard(val position: Int) : MeditationAction()
    data class SelectDuration(val audio: Meditation.Audio) : MeditationAction()
}

sealed class StageAction : Action {
    data class LoadStageList(val isForce: Boolean) : StageAction()
    data class LoadStage(val title: String) : StageAction()
    data class FindActiveStage(val isForce: Boolean) : StageAction()
}
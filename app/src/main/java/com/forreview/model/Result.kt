package com.forreview.model

interface Result

sealed class MeditationsResult : Result {

    sealed class MeditationList : MeditationsResult() {
        data class Success(val meditationList: List<Meditation>) : MeditationList()
        data class Failure(val error: Throwable) : MeditationList()
        object InFlight : MeditationList()
    }

    sealed class LoadMeditationResult : MeditationsResult() {
        data class Success(val meditation: Meditation) : LoadMeditationResult()
        data class Failure(val error: Throwable) : LoadMeditationResult()
        object InFlight : LoadMeditationResult()
    }

    sealed class ActiveMeditation : MeditationsResult() {
        data class Success(val meditation: Meditation) : ActiveMeditation()
        data class Failure(val error: Throwable) : ActiveMeditation()
        data class InFlight(val isInFlight: Boolean) : ActiveMeditation()
    }

    sealed class ActivePaidMeditation : MeditationsResult() {
        data class Success(val meditation: Meditation) : ActivePaidMeditation()
        data class Failure(val error: Throwable) : ActivePaidMeditation()
        data class InFlight(val isInFlight: Boolean) : ActivePaidMeditation()
    }

    sealed class IsAvailableMeditation : MeditationsResult() {
        data class Success(val isAvailable: Boolean) : IsAvailableMeditation()
    }
}

sealed class StageResult : Result {

    sealed class LoadStageList : StageResult() {
        data class Success(val stageList: List<Stage>) : LoadStageList()
        data class Failure(val error: Throwable) : LoadStageList()
        object InFlight : LoadStageList()
    }

    sealed class LoadStage : StageResult() {
        data class Success(val stage: Stage) : LoadStage()
        data class Failure(val error: Throwable) : LoadStage()
        object InFlight : LoadStage()
    }

    sealed class ActiveStage : StageResult() {
        data class Success(val stage: Stage) : ActiveStage()
        data class Failure(val error: Throwable) : ActiveStage()
        object InFlight : ActiveStage()
    }
}

sealed class NavigationResult : Result {
    object Stop : NavigationResult()
    object Complete : NavigationResult()
    object GoToDurationSelection : NavigationResult()
    object GoToMeditation : NavigationResult()
    object GoToMeditationFromDuration : NavigationResult()
    object GoToMeditationFromPreMeditation : NavigationResult()
    object GoNextMeditation : NavigationResult()
    object GoToWakeUpFromMeditation : NavigationResult()
    data class GoToPreMeditationFromDuration(val value: Int) : NavigationResult()
    data class GoToPreMeditation(val value: Int) : NavigationResult()
    data class GoToPreMeditationSelf(val value: Int) : NavigationResult()
    data class GoToPostMeditation(val value: Int) : NavigationResult()
    data class GoToPostMeditationSelf(val value: Int) : NavigationResult()
    data class SetCompleted(val value: Meditation) : NavigationResult()
}



sealed class StatResult : Result {

    sealed class LoadStat : StatResult() {
        data class Success(val stat: Stat) : LoadStat()
    }
}
package com.forreview.ui.main.stages

import com.forreview.model.Event

sealed class StageListEvent: Event {

    object Initial: StageListEvent()

    object Refresh : StageListEvent()

    object GoNextMeditation : StageListEvent()

    object FindActiveStage: StageListEvent()

    object StartMeditation: StageListEvent()

    object IsMeditationAvailable: StageListEvent()
}
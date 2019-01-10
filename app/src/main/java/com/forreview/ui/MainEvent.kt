package com.forreview.ui

import com.forreview.model.Event

sealed class MainEvent: Event {

    object FindActiveStage: MainEvent()
}
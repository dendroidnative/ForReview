package com.forreview.model

interface ViewState

data class ActiveStageViewState(
    val isLoading: Boolean,
    val stage: Stage,
    val error: Throwable?
): ViewState
package com.forreview.ui.main.stages

import com.forreview.model.Stage
import com.forreview.model.ViewState

data class StageListViewState(
    val isLoading: Boolean,
    val stageList: List<Stage>,
    val error: Throwable?
): ViewState
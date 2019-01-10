package com.forreview.ui.menu.more_meditations

import androidx.fragment.app.FragmentActivity
import com.forreview.base.BaseViewModel
import com.forreview.datamanager.DataManager
import com.forreview.helper.CoroutineExecutor
import com.forreview.helper.PrefsHelper
import com.forreview.helper.PurchaseController
import com.forreview.model.*
import com.forreview.model.*
import com.forreview.ui.main.stages.StageListViewState
import com.forreview.ui.menu.skip_stage.MeditationListViewState
import com.forreview.utils.launchSilent
import io.reactivex.Observable

class MoreMeditationsViewModel(
    private val prefsHelper: PrefsHelper,
    private val executor: CoroutineExecutor,
    private val dataManager: DataManager,
    private val purchaseController: PurchaseController
) : BaseViewModel() {
    var title: String? = null

    fun getPurchasedPacks(): MutableList<String> {
        return prefsHelper.getPurchasedPacks()
    }

    fun loadStage(title: String) {
        launchSilent(executor.ui()) {
            dataManager.processAction(StageAction.LoadStage(title))
        }
    }

    fun loadMeditations(title: String) {
        this.title = title
        launchSilent(executor.ui()) {
            dataManager.processAction(MeditationAction.LoadPaidMeditationList(true))
        }
    }

    val singleStageObservable: Observable<ViewState>
        get() = dataManager.stageObservable
            .map(this::viewStateFromResult)

    val meditationObservable: Observable<ViewState>
        get() = dataManager.paidMeditationListObservable
            .map(this::viewStateFromResult)

    private fun viewStateFromResult(result: StageResult): ViewState {
        return when (result) {
            is StageResult.LoadStageList -> {
                when (result) {
                    is StageResult.LoadStageList.Success -> {
                        StageListViewState(false, result.stageList, null)
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
                when (result) {
                    is MeditationsResult.MeditationList.Success -> MeditationListViewState(
                        false,
                        result.meditationList.filter { it.stage.title == title },
                        null
                    )
                    is MeditationsResult.MeditationList.Failure -> TODO()
                    MeditationsResult.MeditationList.InFlight -> TODO()
                }
            }
            is MeditationsResult.ActiveMeditation -> {
                TODO("not implemented")
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

    fun setPaidActiveMeditation(item: Meditation) {
        dataManager.setActivePaidMeditation(item)
    }

    fun requestPurchase(
        activity: FragmentActivity,
        sku: String,
        billingInterface: PurchaseController.BillingInterface
    ) {
        purchaseController.requestPurchase(activity, sku, billingInterface)
    }
}
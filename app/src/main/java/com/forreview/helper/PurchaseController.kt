package com.forreview.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.*
import com.forreview.*

class PurchaseController(private val context: Context, private val prefsHelper: PrefsHelper) :
    PurchasesUpdatedListener {

    private lateinit var billingClient: BillingClient
    private var billingOk: BillingInterface? = null

    @SuppressLint("SwitchIntDef")
    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        if (responseCode == BillingResponse.OK && purchases != null) {
            when (responseCode) {
                BillingResponse.OK -> {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                }
                BillingResponse.ITEM_ALREADY_OWNED -> {
                    billingOk?.onBillingFailure("ITEM_ALREADY_OWNED")
                    billingOk?.alreadyOwned()
                }
                else -> {
                    billingOk?.onBillingFailure("ERROR")
                }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        when (purchase.sku) {
            SKU_ANXIETY_PACK -> prefsHelper.activateAnxietyPack()
            SKU_CALM_ON_THE_GO -> prefsHelper.activateCalmOnTheGoPack()
            SKU_END_OF_DAY_PACK -> prefsHelper.activateEndOfDayPack()
            SKU_RECENTERING_PACK -> prefsHelper.activateRecenteringPack()
            SKU_EVERYTHING_PACK -> prefsHelper.activateEverythingPack()
        }
        billingOk?.purchaseSuccess()
    }

    fun init(billingOk: BillingInterface) {
        billingClient = newBuilder(context).setListener(this).build()
        this.billingOk = billingOk
        billingClient.startConnection(object : BillingClientStateListener {
            @SuppressLint("SwitchIntDef")
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                when (billingResponseCode) {
                    BillingResponse.OK -> {
                        billingOk.onBillingOK()
                    }
                    else -> {
                        billingOk.onBillingFailure("FAILURE")
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                billingOk.onBillingFailure("disconnected")
            }
        })
    }

    fun requestPurchase(activity: Activity, sku: String, billingInterface: BillingInterface) {
        val flowParams = BillingFlowParams.newBuilder()
            .setSku(sku)
            .setType(SkuType.INAPP) // SkuType.SUB for subscription
            .build()
        billingOk = billingInterface
        billingClient.launchBillingFlow(activity, flowParams)
    }

    fun checkPurchases() {
        billingClient.queryPurchaseHistoryAsync(SkuType.INAPP) { responseCode, purchasesList ->
            if (responseCode == BillingResponse.OK && purchasesList != null) {
                for (purchase in purchasesList) {
                    when (purchase.sku) {
                        SKU_ANXIETY_PACK -> prefsHelper.activateAnxietyPack()
                        SKU_CALM_ON_THE_GO -> prefsHelper.activateCalmOnTheGoPack()
                        SKU_END_OF_DAY_PACK -> prefsHelper.activateEndOfDayPack()
                        SKU_RECENTERING_PACK -> prefsHelper.activateRecenteringPack()
                        SKU_EVERYTHING_PACK -> prefsHelper.activateEverythingPack()
                    }
                }
            }
        }
    }

    fun checkPurchaseFromPurchase() {
        billingClient.queryPurchaseHistoryAsync(SkuType.INAPP) { responseCode, purchasesList ->
            if (responseCode == BillingResponse.OK && purchasesList != null) {
                for (purchase in purchasesList) {
                    when (purchase.sku) {
                        SKU_ANXIETY_PACK -> prefsHelper.activateAnxietyPack()
                        SKU_CALM_ON_THE_GO -> prefsHelper.activateCalmOnTheGoPack()
                        SKU_END_OF_DAY_PACK -> prefsHelper.activateEndOfDayPack()
                        SKU_RECENTERING_PACK -> prefsHelper.activateRecenteringPack()
                        SKU_EVERYTHING_PACK -> prefsHelper.activateEverythingPack()
                    }
                }
                billingOk?.alreadyOwned()
            }
        }
    }

    interface BillingInterface {
        fun onBillingOK()
        fun onBillingFailure(message: String)
        fun purchaseSuccess()
        fun alreadyOwned()
    }
}
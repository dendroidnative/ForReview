package com.forreview

import android.app.Application
import android.util.Log
import com.facebook.stetho.Stetho
import com.forreview.di.moduleList
import com.forreview.helper.PurchaseController
import com.forreview.BuildConfig
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin
import timber.log.Timber
import java.net.ConnectException

class MyApplication: Application(), PurchaseController.BillingInterface {
    override fun purchaseSuccess() {

    }

    override fun onBillingOK() {
        purchaseController.checkPurchases()
    }

    override fun onBillingFailure(message: String) {

    }

    override fun alreadyOwned() {
        purchaseController.checkPurchases()
    }
    private val purchaseController by inject<PurchaseController> ()

    override fun onCreate() {
        super.onCreate()
        initTimber()
        initStetho()
        startKoin(this, moduleList)

        initBilling()

        RxJavaPlugins.setErrorHandler {
            Timber.w(it, "My uncaught error")
        }
    }

    private fun initStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
        return
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                        return
                    }
                    if (t != null && t !is ConnectException) {
                        try {
//                            Crashlytics.logException(t)
//                            FirebaseCrash.report(t)
                        } catch (ignore: Exception) {
                            //ignore
                        }
                    }
                }
            })
        }
    }

    private fun initBilling(){
        purchaseController.init(this)
    }
}
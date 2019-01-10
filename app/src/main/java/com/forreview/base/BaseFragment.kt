package com.forreview.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.forreview.helper.LoadingUiHelper
import com.forreview.model.ViewState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment: Fragment() {

    protected lateinit var baseActivity: BaseActivity
    private var compositeDisposable: CompositeDisposable? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        baseActivity = context as BaseActivity
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compositeDisposable = CompositeDisposable()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dispose()
    }

    fun showError(error: String?) {
        baseActivity.showError(error)
    }

    fun showError(@StringRes error: Int) {
        baseActivity.showError(error)
    }

    fun showError(view: View, error: String?) {
        baseActivity.showError(view, error)
    }

    fun showError(view: View, @StringRes error: Int) {
        baseActivity.showError(view, error)
    }

    fun showMessage(message: String?) {
        baseActivity.showMessage(message)
    }

    fun showMessage(@StringRes message: Int) {
        baseActivity.showMessage(message)
    }

    fun showMessage(view: View, message: String?) {
        baseActivity.showMessage(view, message)
    }

    fun showMessage(view: View, @StringRes message: Int) {
        baseActivity.showMessage(view, message)
    }

    fun showLoading(isShow: Boolean, type: LoadingUiHelper.Type = LoadingUiHelper.Type.FULL_SCREEN) {
        baseActivity.showLoading(isShow, type)
    }

    protected fun subscribe(observable: Observable<ViewState>) {
        compositeDisposable?.add(observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                onApplyViewState(it)
            })
    }

    protected fun dispose() {
        compositeDisposable?.dispose()
        compositeDisposable = null
    }

    protected open fun onApplyViewState(viewState: ViewState) {

    }
}
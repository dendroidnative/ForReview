package com.forreview.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.forreview.helper.LoadingUiHelper
import com.forreview.model.ViewState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

abstract class BaseBottomDialogFragment: BottomSheetDialogFragment() {

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
        compositeDisposable?.dispose()
        compositeDisposable = null
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

    fun showMessage(error: String?) {
        baseActivity.showMessage(error)
    }

    fun showMessage(@StringRes error: Int) {
        baseActivity.showMessage(error)
    }

    fun showMessage(view: View, error: String?) {
        baseActivity.showMessage(view, error)
    }

    fun showMessage(view: View, @StringRes error: Int) {
        baseActivity.showMessage(view, error)
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

    protected open fun onApplyViewState(viewState: ViewState) {

    }
}
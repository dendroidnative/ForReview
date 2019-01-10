package com.forreview.base

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import com.forreview.helper.LoadingUiHelper

abstract class BaseDialogFragment: DialogFragment() {

    protected lateinit var baseActivity: BaseActivity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        baseActivity = context as BaseActivity
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
}
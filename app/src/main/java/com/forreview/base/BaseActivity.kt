package com.forreview.base

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.forreview.helper.LoadingUiHelper
import com.forreview.utils.DimensUtils
import com.forreview.R
import com.forreview.model.ViewState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

abstract class BaseActivity: AppCompatActivity() {

    private var progressDialog: LoadingUiHelper.ProgressDialogFragment? = null

    private var compositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compositeDisposable = CompositeDisposable()
        progressDialog = supportFragmentManager.findFragmentByTag(LoadingUiHelper.ProgressDialogFragment.TAG)
                as LoadingUiHelper.ProgressDialogFragment?
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose()
    }

    fun showLoading(isShow: Boolean, type: LoadingUiHelper.Type = LoadingUiHelper.Type.FULL_SCREEN) {
        if (isShow) {
            if (progressDialog == null) {
                progressDialog = LoadingUiHelper.showProgress(supportFragmentManager, type)
            }
        } else {
            progressDialog?.dismiss()
            progressDialog = null
        }
    }

    fun setTitle(title: String) {
        titleTextView.text = title
    }

    fun showError(@StringRes error: Int, anchor: View? = null) {
        showError(getString(error), anchor)
    }

    fun showError(error: String?, anchor: View? = null) {
        error?: return
        showMessage(error, anchor, Color.parseColor("#F44336"))
    }





    fun showError(view: View, error: String?) {
        error?:return
        Snackbar.make(view, Html.fromHtml("<font color=\"#F44336\">$error</font>"), Snackbar.LENGTH_LONG).show()
    }

    fun showError(view: View, @StringRes error: Int) {
        showError(view, getString(error))
    }




    fun showMessage(@StringRes message: Int, anchor: View? = null, textColor: Int = Color.WHITE) {
        showMessage(getString(message), anchor, textColor)
    }

    fun showMessage(message: String?, anchor: View? = null, textColor: Int = Color.WHITE) {
        message?: return
        var yOffset = 0

        if(anchor != null) {
            val anchorParent = anchor.parent as? View?
            if (anchorParent != null) {
                yOffset = anchorParent.height - anchor.y.toInt() + DimensUtils.dpToPx(0f)
            }
        }

        val toast = Toast(this)
        toast.setGravity(Gravity.BOTTOM, 0, yOffset)

        val inflate = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflate.inflate(R.layout.toast, null)
        val tv = v.findViewById(R.id.message) as TextView
        tv.text = message
        tv.setTextColor(textColor)

        toast.view = v
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }





    fun showMessage(view: View, @StringRes message: Int) {
        showMessage(view, getString(message))
    }

    fun showMessage(view: View, message: String?) {
        message?: return
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
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
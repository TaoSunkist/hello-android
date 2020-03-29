package com.zhimeng.battery.ui.reusable.controller

import android.view.View
import com.zhimeng.battery.ui.reusable.viewcontroller.controller.ViewController
import com.zhimeng.battery.utilities.Dimens
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewController : ViewController() {

    val compositeDisposable = CompositeDisposable()

    override fun viewWillUnload(view: View) {
        super.viewWillUnload(view)
        this.compositeDisposable.clear()
    }

    fun fitSafeArea(contentView: View) {
        contentView.setPadding(0, Dimens.safeArea.top, 0, Dimens.safeArea.bottom)
    }

    fun showProgressDialog(message: String = "") {
        activity.showProgressDialog(message = message)
    }

    fun showProgressDialog(messageRes: Int) {
        activity.showProgressDialog(messageRes = messageRes)
    }

    fun dismissProgressDialog(animated: Boolean = true, completion: (() -> Unit)? = null) {
        activity.dismissProgressDialog(animated = animated, completion = completion)
    }
}
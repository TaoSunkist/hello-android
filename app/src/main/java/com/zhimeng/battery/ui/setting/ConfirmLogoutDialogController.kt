package com.zhimeng.battery.ui.setting

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.zhimeng.battery.R
import com.zhimeng.battery.data.UserStore
import com.zhimeng.battery.data.service.UserService
import com.zhimeng.battery.ui.reusable.controller.BaseViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentingAnimation
import com.zhimeng.battery.utilities.observeOnMainThread
import es.dmoral.toasty.Toasty
import io.reactivex.rxkotlin.addTo

class ConfirmLogoutDialogController : BaseViewController() {

    companion object {
        const val TAG = "confirmDialog"
    }

    init {
        presentationStyle.apply {
            animation = PresentingAnimation.RIGHT_TRANSLATION
            fullscreen = false
            gravity = Gravity.CENTER
            allowDismiss = true
            tag = TAG
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.dialog_confirm_logout, container, false)
    }

    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)
        view.findViewById<Button>(R.id.dialog_confirm_log_yes_button).setOnClickListener {
            doLogout()
        }
        view.findViewById<Button>(R.id.dialog_confirm_log_no_button).setOnClickListener {
            navigationController?.popViewController(animated = true)
        }
    }

    private fun doLogout() {
        showProgressDialog()
        UserService.shared.logout()
            .observeOnMainThread(onError = {
                Toasty.normal(
                    context,
                    it.localizedMessage ?: context.getString(R.string.unkonwn_failed_tip)
                ).show()
            }, onTerminate = {
                dismissProgressDialog(completion = {
                    UserStore.shared.logout()
                })
            }).addTo(compositeDisposable)
    }
}
package com.zhimeng.battery.ui.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import com.zhimeng.battery.BuildConfig
import com.zhimeng.battery.R
import com.zhimeng.battery.app.BatteryApplication
import com.zhimeng.battery.data.model.PayType
import com.zhimeng.battery.data.service.PaymentService
import com.zhimeng.battery.ui.reusable.controller.BaseViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentingAnimation
import com.zhimeng.battery.ui.reusable.viewcontroller.util.annotation.ViewRes
import com.zhimeng.battery.ui.reusable.views.TitleBar
import com.zhimeng.battery.ui.setting.WasPaidPromptViewController
import com.zhimeng.battery.utilities.observeOnMainThread
import com.zhimeng.battery.utilities.weak
import io.reactivex.rxkotlin.addTo

data class PaymentSelectionPreloadModel(
    val itemId: Int,
    val price: String,
    val description: String,
    val amountOfMoney: Int
)

interface PaymentSelectionViewControllerDelegate {
    fun paymentSelectionViewControllerPurchaseSuccess()
}

class PaymentSelectionViewController() :
    BaseViewController() {

    companion object {
        const val TAG = "tagPayments"
    }

    @ViewRes(res = R.id.view_controller_payment_selection_pay_checkbox)
    lateinit var wxpayCheckBox: AppCompatCheckBox

    @ViewRes(res = R.id.view_controller_payment_selection_confirm_button)
    lateinit var confirmButton: AppCompatButton

    @ViewRes(res = R.id.view_controller_titlebar)
    lateinit var titleBar: TitleBar

    var delegate: PaymentSelectionViewControllerDelegate? by weak()

    init {
        presentationStyle.apply {
            animation = PresentingAnimation.BOTTOM_FADE
            tag = TAG
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.view_controller_payment_selection, container, false)
    }

    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)
        wxpayCheckBox.isChecked = true

        wxpayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            confirmButton.isEnabled = isChecked
        }

        wxpayCheckBox.setOnClickListener {
        }
        titleBar.onClickerToLeft { dismiss(animated = true) }
        confirmButton.setOnClickListener { confirmButtonPressed() }

        /* 开发环境下，长按当成成功，测试方便 */
        if (BuildConfig.DEBUGGING_MODE) {
            confirmButton.setOnLongClickListener {
                showProgressDialog("Paying...")
                BatteryApplication.MAIN_HANDLER.postDelayed({
                    onPaymentSuccess()
                }, 1000)
                true
            }
        }
    }

    private fun confirmButtonPressed() {
        showProgressDialog("请稍后...")
        PaymentService.shared.payments(orderNumber = "", payType = PayType.DEPOSIT)
            .observeOnMainThread(onSuccess = {
                /* TODO 返回主页面 */
                present(viewController = WasPaidPromptViewController(), animated = true)
            }, onError = {
            }, onTerminate = {
                dismissProgressDialog()
            }).addTo(compositeDisposable = compositeDisposable)
    }

    private fun onPaymentSuccess() {
        TODO("long click got successfully.")
    }
}
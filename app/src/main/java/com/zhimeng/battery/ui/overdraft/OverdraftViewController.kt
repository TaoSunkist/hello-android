package com.zhimeng.battery.ui.overdraft

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.mooveit.library.Fakeit
import com.zhimeng.battery.R
import com.zhimeng.battery.data.model.OrderDetail
import com.zhimeng.battery.data.model.PayType
import com.zhimeng.battery.data.model.UserStatus
import com.zhimeng.battery.data.service.PaymentService
import com.zhimeng.battery.ui.reusable.controller.BaseViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentationStyle
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentingAnimation
import com.zhimeng.battery.ui.reusable.viewcontroller.util.annotation.ViewRes
import com.zhimeng.battery.ui.reusable.views.TitleBar
import com.zhimeng.battery.utilities.observeOnMainThread
import com.zhimeng.battery.utilities.weak
import es.dmoral.toasty.Toasty
import io.reactivex.rxkotlin.addTo
import kotlin.random.Random

data class OverdraftUIModel(
    var costAltogether: String,
    var chargeDuration: String,
    var payChannel: String,
    var orderid: String,
    var powerid: String,
    var leaseDate: String,
    var leaseAddress: String,
    var giveBackDate: String,
    var giveBackAddress: String,
    var qrcodeChannel: String
) {
    companion object {
        fun fake(): OverdraftUIModel {
            return OverdraftUIModel(
                costAltogether = Random.nextInt(1000).toString(),
                chargeDuration = Random.nextLong(10000).toString(),
                payChannel = Fakeit.providers.keys.random(),
                orderid = System.currentTimeMillis().toString(),
                powerid = System.currentTimeMillis().toString(),
                leaseDate = Fakeit.dateTime().dateTimeFormatter(),
                leaseAddress = Fakeit.address().streetAddress(),
                giveBackDate = Fakeit.dateTime().dateTimeFormatter(),
                giveBackAddress = Fakeit.address().streetAddress(),
                qrcodeChannel = Fakeit.book().publisher()
            )
        }

        fun init(
            userStatus: UserStatus,
            orderDetail: OrderDetail
        ): OverdraftUIModel {
            return OverdraftUIModel(
                costAltogether = (userStatus.paymentData?.cost ?: 0).toString(),
                chargeDuration = (userStatus.paymentData?.duration ?: 0).toString(),
                payChannel = (userStatus.paymentData?.paymentMethod?.name ?: ""),
                orderid = userStatus.paymentData?.orderNumber ?: "",
                powerid = orderDetail.terminalId,
                leaseDate = userStatus.paymentData?.borrowStartTime ?: "",
                leaseAddress = "",
                giveBackDate = orderDetail.returnTime,
                giveBackAddress = orderDetail.returnAddress,
                qrcodeChannel = ""
            )
        }
    }
}

interface OverdraftViewControllerDelegate {
    fun overdraftViewControllerDelegateDidPaid()
}

class OverdraftViewController(val uiModel: OverdraftUIModel) : BaseViewController() {

    companion object {
        const val TAG_AUTHENTICATION = "overdraft"
    }

    @ViewRes(res = R.id.view_controller_titlebar)
    lateinit var titleBar: TitleBar


    @ViewRes(res = R.id.view_controller_overdraft_status_tip_textview)
    lateinit var statusTipTextView: AppCompatTextView

    @ViewRes(res = R.id.view_controller_overdraft_cost_altogether_textview)
    lateinit var costAltogetherTextview: AppCompatTextView

    @ViewRes(res = R.id.view_controller_overdraft_charge_duration_textview)
    lateinit var chargeDurationTextview: AppCompatTextView

    @ViewRes(res = R.id.view_controller_overdraft_pay_channel_textview)
    lateinit var payChannelTextview: AppCompatTextView

    @ViewRes(res = R.id.view_controller_overdraft_orderid_textview)
    lateinit var orderidTextview: AppCompatTextView

    @ViewRes(res = R.id.view_controller_overdraft_powerid_textview)
    lateinit var poweridTextview: AppCompatTextView

    @ViewRes(res = R.id.view_controller_overdraft_lease_date_textview)
    lateinit var leaseDateTextview: AppCompatTextView

    @ViewRes(res = R.id.view_controller_overdraft_lease_address_textview)
    lateinit var leaseAddressTextview: AppCompatTextView

    @ViewRes(res = R.id.view_controller_overdraft_give_back_date_textview)
    lateinit var giveBackDateTextview: AppCompatTextView

    @ViewRes(res = R.id.view_controller_overdraft_give_back_address_textview)
    lateinit var giveBackAddressTextview: AppCompatTextView

    @ViewRes(res = R.id.view_controller_overdraft_qrcode_channel_textview)
    lateinit var qrcodeChannelTextview: AppCompatTextView

    @ViewRes(res = R.id.view_controller_overdraft_to_pay_button)
    lateinit var toPayButton: View

    var delegate: OverdraftViewControllerDelegate? by weak()

    init {
        tag = TAG_AUTHENTICATION
        presentationStyle = PresentationStyle(
            animation = PresentingAnimation.BOTTOM
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.view_controller_overdraft, container, false)
    }

    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)
        titleBar.onClickerToLeft { dismiss(animated = true) }

        chargeDurationTextview.text = uiModel.chargeDuration
        costAltogetherTextview.text = uiModel.costAltogether

        leaseAddressTextview.text = uiModel.leaseAddress
        leaseDateTextview.text = uiModel.leaseDate

        poweridTextview.text = uiModel.powerid
        orderidTextview.text = uiModel.orderid

        payChannelTextview.text = uiModel.payChannel
        qrcodeChannelTextview.text = uiModel.qrcodeChannel

        giveBackAddressTextview.text = uiModel.giveBackAddress
        giveBackDateTextview.text = uiModel.giveBackDate

        toPayButton.setOnClickListener { toPay() }

    }

    private fun toPay() {
        showProgressDialog()
        PaymentService.shared.payments(orderNumber = uiModel.orderid, payType = PayType.OVERDUE)
            .observeOnMainThread(onSuccess = {
                /*TODO 修改订单状态*/
                testPay(uiModel)
            }, onError = {
                Toasty.normal(context, it.localizedMessage ?: "unknown falied").show()
                dismissProgressDialog()
            }, onTerminate = { })
            .addTo(compositeDisposable = compositeDisposable)

    }

    private fun testPay(it: OverdraftUIModel) {
        PaymentService.shared.alterOrderStatus(orderNumber = it.orderid)
            .observeOnMainThread(onSuccess = {
                dismiss(animated = true)
            }, onError = { dismissProgressDialog() })
            .addTo(compositeDisposable = compositeDisposable)
    }
}
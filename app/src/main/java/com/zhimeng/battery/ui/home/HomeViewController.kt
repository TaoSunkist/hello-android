package com.zhimeng.battery.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhimeng.battery.R
import com.zhimeng.battery.data.UserStore
import com.zhimeng.battery.data.model.Status
import com.zhimeng.battery.data.model.UserStatus
import com.zhimeng.battery.data.service.PaymentService
import com.zhimeng.battery.data.service.UserService
import com.zhimeng.battery.ui.ChargingController
import com.zhimeng.battery.ui.ChargingUIModel
import com.zhimeng.battery.ui.overdraft.OverdraftUIModel
import com.zhimeng.battery.ui.overdraft.OverdraftViewController
import com.zhimeng.battery.ui.reusable.controller.BaseViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.util.annotation.ViewRes
import com.zhimeng.battery.ui.scanqrcode.ScanQRCodeViewController
import com.zhimeng.battery.ui.setting.ConfirmLogoutDialogController
import com.zhimeng.battery.ui.waspaid.WasPaidViewController
import com.zhimeng.battery.utilities.Dimens
import com.zhimeng.battery.utilities.PermissionRequester
import com.zhimeng.battery.utilities.observeOnMainThread
import es.dmoral.toasty.Toasty
import io.reactivex.rxkotlin.addTo

val homeBottomTabHeight = Dimens.dpToPx(64)

class HomeViewController : BaseViewController() {


    @ViewRes(res = R.id.view_controller_home_exit_textview)
    lateinit var exitView: View

    @ViewRes(res = R.id.view_controller_home_profile_textview)
    lateinit var profileView: View

    @ViewRes(res = R.id.view_controller_home_scan_qr_code_textview)
    lateinit var scanQrCodeView: View

    init {
        tag = "home"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.view_controller_home, container, false)
    }

    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)

        scanQrCodeView.setOnClickListener {
            showProgressDialog()
            UserService.shared.usersStatus()
                .observeOnMainThread(onSuccess = {
                    when (it.status) {
                        Status.USING -> {/* 使用中 */
                            val controller = ChargingController(uiModel = ChargingUIModel.init(it))
                            present(viewController = controller, animated = true)
                        }
                        Status.OVERDRAFT -> {
                            queryOrderDetail(it)
                        }
                        Status.FINISH -> {
                            queryOrderDetail(it)
                        }
                        Status.OVERDUE_SETTLEMENT -> {
                        }
                        else -> {
                            processUserStatus()
                        }
                    }
                }, onError = {
                }, onTerminate = {
                    dismissProgressDialog()
                }).addTo(compositeDisposable = compositeDisposable)
        }
        exitView.setOnClickListener {
            val controller = ConfirmLogoutDialogController()
            navigationController?.push(viewController = controller, animated = true)
        }
    }

    private fun queryOrderDetail(userStatus: UserStatus) {
        PaymentService.shared.fetchOrderDetail(
            orderNumber = userStatus.paymentData?.orderNumber ?: ""
        ).observeOnMainThread(onSuccess = { orderDetail ->
            val controller = OverdraftViewController(
                OverdraftUIModel.init(
                    userStatus = userStatus,
                    orderDetail = orderDetail
                )
            )
            present(viewController = controller, animated = true)
        }, onError = {
            Toasty.normal(context, it.localizedMessage ?: "").show()
        }, onTerminate = { dismissProgressDialog() })
            .addTo(compositeDisposable = compositeDisposable)
    }

    private fun processUserStatus() {
        PermissionRequester.getCamera(activity).observeOnMainThread(onSuccess = {
            present(viewController = ScanQRCodeViewController(), animated = true)
        }, onError = {
            Toasty.normal(context, "权限获取失败, 请授权相机使用权限").show()
        }).addTo(compositeDisposable = compositeDisposable)
    }
}
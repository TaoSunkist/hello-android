package com.zhimeng.battery.ui.scanqrcode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import cn.bingoogolapple.qrcode.core.QRCodeView
import cn.bingoogolapple.qrcode.zxing.ZXingView
import com.zhimeng.battery.R
import com.zhimeng.battery.data.model.BoxInfomation
import com.zhimeng.battery.data.model.BoxStatus
import com.zhimeng.battery.data.model.Status
import com.zhimeng.battery.data.model.UserStatus
import com.zhimeng.battery.data.service.PaymentService
import com.zhimeng.battery.data.service.StationService
import com.zhimeng.battery.data.service.UserService
import com.zhimeng.battery.ui.ChargingController
import com.zhimeng.battery.ui.ChargingUIModel
import com.zhimeng.battery.ui.gallery.GalleryViewController
import com.zhimeng.battery.ui.gallery.GalleryViewControllerDelegate
import com.zhimeng.battery.ui.overdraft.OverdraftUIModel
import com.zhimeng.battery.ui.overdraft.OverdraftViewController
import com.zhimeng.battery.ui.overdraft.OverdraftViewControllerDelegate
import com.zhimeng.battery.ui.reusable.controller.BaseViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentationStyle
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentingAnimation
import com.zhimeng.battery.ui.reusable.viewcontroller.util.annotation.ViewRes
import com.zhimeng.battery.ui.todeposit.ToDepositeViewController
import com.zhimeng.battery.ui.waspaid.WasPaidViewController
import com.zhimeng.battery.utilities.PermissionRequester
import com.zhimeng.battery.utilities.observeOnMainThread
import com.zhimeng.battery.utilities.printf
import es.dmoral.toasty.Toasty
import io.reactivex.rxkotlin.addTo
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class ScanQRCodeViewController : BaseViewController(), QRCodeView.Delegate,
    GalleryViewControllerDelegate, OverdraftViewControllerDelegate {

    companion object {
        const val TAG_AUTHENTICATION = "ScanQRCode"
    }

    @ViewRes(R.id.view_controller_scan_qr_code_zxingview)
    lateinit var zxingView: ZXingView

    @ViewRes(R.id.view_controller_scan_qr_code_back_imageview)
    lateinit var backImageView: View

    @ViewRes(R.id.view_controller_scan_qr_code_album_imageview)
    lateinit var albumImageView: View

    @ViewRes(R.id.view_controller_scan_qr_code_light_imageview)
    lateinit var lightImageView: View

    init {
        tag = TAG_AUTHENTICATION
        presentationStyle = PresentationStyle(
            animation = PresentingAnimation.BOTTOM
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.view_controller_scanqrcode, container, false)
    }

    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)
        zxingView.setDelegate(this)
        zxingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        albumImageView.setOnClickListener { choosePhoto() }
        lightImageView.setOnClickListener { openLight() }
        backImageView.setOnClickListener { dismiss(animated = true) }
    }

    private var isOpenZXingViewFlashlight: AtomicBoolean = AtomicBoolean(false)

    private fun openLight() {
        if (isOpenZXingViewFlashlight.getAndSet(isOpenZXingViewFlashlight.get().not())) {
            zxingView.closeFlashlight()
        } else {
            zxingView.openFlashlight()
        }
    }

    private fun choosePhoto() {
        PermissionRequester.getReadExternalStorage(context as AppCompatActivity)
            .observeOnMainThread(
                onSuccess = {
                    val controller = GalleryViewController()
                    controller.delegate = this
                    present(viewController = controller, animated = true)
                },
                onError = { Toasty.normal(context, "无法访问系统相册").show() }
            ).addTo(compositeDisposable = compositeDisposable)
    }

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)
        zxingView.startCamera()
    }

    override fun viewWillDisappear(animated: Boolean) {
        super.viewWillDisappear(animated)
    }

    override fun doDestroyView(container: ViewGroup) {
        super.doDestroyView(container)
        zxingView.onDestroy()
    }

    override fun onScanQRCodeSuccess(result: String) {
        val boxid = result.substring(result.lastIndexOf('/') + 1, result.length)
        /*请求机柜码*/
        StationService.shared.queryBoxInfomation(boxid).observeOnMainThread(onSuccess = {
            processBoxInfomation(it)
        }, onError = {
            Toasty.normal(
                context,
                it.localizedMessage ?: context.getString(R.string.bad_network_failed_tip)
            ).show()
        }, onTerminate = {
            dismissProgressDialog(animated = true)
        }).addTo(compositeDisposable = compositeDisposable)
    }

    private fun processBoxInfomation(boxInfomation: BoxInfomation) {
        if (boxInfomation.hasOverdure) {
            /*TODO 有欠款, 借口查询*/
            queryOverdureDetail(boxInfomation)
        } else if (boxInfomation.hasDeposit.not()) {
            /*TODO 无欠款 无押金*/
            val controller = ToDepositeViewController()
            present(viewController = controller, animated = true)
        } else {
            if (boxInfomation.station.status == BoxStatus.ONLINE) {
                queryOverdureDetail(boxInfomation)
            } else if (boxInfomation.station.status == BoxStatus.NOT_FIND) {
                Toasty.normal(context, "BoxStatus.NOT_FIND").show()
            } else if (boxInfomation.station.status == BoxStatus.REPAIR) {
                Toasty.normal(context, "BoxStatus.REPAIR").show()
            } else if (boxInfomation.station.status == BoxStatus.TIMEOUT) {
                Toasty.normal(context, "BoxStatus.TIMEOUT").show()
            } else {
                Toasty.normal(context, R.string.bad_network_failed_tip).show()
                dismiss(animated = true)
            }
        }
    }

    private fun queryOverdureDetail(boxInfomation: BoxInfomation) {
        showProgressDialog()
        UserService.shared.usersStatus()
            .observeOnMainThread(onSuccess = {
                if (it.status == Status.USING) {/* 使用中 */
                    val controller = ChargingController(uiModel = ChargingUIModel.init(it))
                    present(viewController = controller, animated = true)
                    dismissProgressDialog()
                } else {
                    /*TODO 已经充值押金*/
                    val controller = WasPaidViewController(boxId = boxInfomation.station.boxId)
                    present(viewController = controller, animated = true)
                    queryOrderDetail(it)
                }
            }, onError = {
                dismissProgressDialog()
            }, onTerminate = {
            }).addTo(compositeDisposable = compositeDisposable)
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

    /*摄像头环境亮度发生变化*/
    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {}

    override fun onScanQRCodeOpenCameraError() = Toasty.normal(context, "相机打开出错").show()

    override fun galleryViewControllerShouldDismiss() = dismiss(animated = true)

    override fun galleryViewControllerDidSelect(file: File) {
        dismiss(animated = true, completion = {
            printf("galleryViewControllerDidSelect ${file.path}")
            if (file.exists().not()) {
                Toasty.normal(context, "文件不存在").show()
            } else {
                zxingView.decodeQRCode(file.path)
            }
            showProgressDialog()
        })
    }

    override fun overdraftViewControllerDelegateDidPaid() {
        dismissProgressDialog(animated = false, completion = { dismiss(animated = true) })
    }
}
package com.zhimeng.battery.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import com.mooveit.library.Fakeit
import com.zhimeng.battery.R
import com.zhimeng.battery.data.model.Status
import com.zhimeng.battery.data.model.UserStatus
import com.zhimeng.battery.data.service.UserService
import com.zhimeng.battery.ui.reusable.controller.BaseViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentationStyle
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentingAnimation
import com.zhimeng.battery.ui.reusable.viewcontroller.util.annotation.ViewRes
import com.zhimeng.battery.ui.reusable.views.TitleBar
import com.zhimeng.battery.utilities.MainThread
import com.zhimeng.battery.utilities.PermissionRequester
import com.zhimeng.battery.utilities.observeOnMainThread
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

data class ChargingUIModel(
    val duration: Int,
    val cost: Float,
    val address: String
) {
    companion object {
        fun fake(): ChargingUIModel {
            return ChargingUIModel(
                duration = (10000..50000).random(),
                cost = (100..500).random().toFloat(),
                address = Fakeit.address().streetAddress()
            )
        }

        fun init(userStatus: UserStatus): ChargingUIModel {
            return ChargingUIModel(
                duration = userStatus.usageData?.duration ?: 0,
                cost = userStatus.usageData?.amount ?: 0f,
                address = userStatus.usageData?.borrowAddress ?: ""
            )
        }
    }
}

class ChargingController(private var uiModel: ChargingUIModel) : BaseViewController() {

    companion object {
        const val TAG_AUTHENTICATION = "charging"
    }

    @ViewRes(res = R.id.view_controller_titlebar)
    lateinit var titlebar: TitleBar


    @ViewRes(res = R.id.view_controller_charging_duration_textview)
    lateinit var durationTextview: AppCompatTextView

    @ViewRes(res = R.id.view_controller_charging_cost_textview)
    lateinit var costTextview: AppCompatTextView

    @ViewRes(res = R.id.view_controller_charging_address_textview)
    lateinit var addressTextview: AppCompatTextView

    @ViewRes(res = R.id.view_controller_charging_standard_textview)
    lateinit var standardTextview: AppCompatTextView

    @ViewRes(res = R.id.view_controller_charging_call_phone_artificial_textview)
    lateinit var callPhoneArficialTextView: AppCompatTextView

    init {
        tag = TAG_AUTHENTICATION
        presentationStyle = PresentationStyle(
            animation = PresentingAnimation.BOTTOM
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.view_controller_charging, container, false)
    }

    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)
        titlebar.onClickerToLeft { dismiss(animated = true) }
        setupView()
        callPhoneArficialTextView.setOnClickListener {
            PermissionRequester.getCallPhone(activity)
                .observeOnMainThread(onSuccess = {
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:13901087640")
                    if (ActivityCompat.checkSelfPermission(
                            context, Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) context.startActivity(intent)
                }, onError = {
                }, onTerminate = {}).addTo(compositeDisposable = compositeDisposable)
        }
        /* 每隔一秒刷新一下用户状态 */
        Observable.interval(30, TimeUnit.SECONDS).subscribeOn(MainThread)
            .subscribe {
                UserService.shared.usersStatus()
                    .observeOnMainThread(onSuccess = {
                        println("$it")
                        uiModel = ChargingUIModel.init(it)
                        setupView()
                    }, onError = {
                        dismissProgressDialog()
                    }, onTerminate = {
                    }).addTo(compositeDisposable = compositeDisposable)
            }.addTo(compositeDisposable)
    }

    private fun setupView() {
        durationTextview.text = uiModel.duration.toString()
        costTextview.text = uiModel.cost.toString()
        addressTextview.text = uiModel.address
    }

    override fun onBackPressed(): Boolean {
        /* 不允许go back */
        navigationController?.popToRoot()
        dismiss(animated = true)
        return true
    }
}
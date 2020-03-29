package com.zhimeng.battery.ui.common.status

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.zhimeng.battery.R
import com.zhimeng.battery.ui.reusable.controller.BaseViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentingAnimation
import com.zhimeng.battery.ui.reusable.viewcontroller.util.annotation.ViewRes
import com.zhimeng.battery.ui.reusable.views.TitleBar
import com.zhimeng.battery.utilities.Dimens

enum class FailedType(@DrawableRes val iconResId: Int, @StringRes val tipsResId: Int) {
    /*设备没有联网*/
    NETWORK(
        iconResId = R.drawable.ic_unavailable_network_failed,
        tipsResId = R.string.unavailable_network_failed_tip
    ),

    /*等待维修*/
    FIXING(
        iconResId = R.drawable.ic_fixing_failed,
        tipsResId = R.string.fixing_failed_tip
    ),

    /*网络不好*/
    UNAVAILABLE(
        iconResId = R.drawable.ic_bad_network_failed,
        tipsResId = R.string.bad_network_failed_tip
    )
}

data class FailedTipsUIModel(val failedType: FailedType) {
    companion object {
        fun fake(): FailedTipsUIModel {
            val failedType = FailedType.values().random()
            return FailedTipsUIModel(failedType)
        }

        fun init(): FailedTipsUIModel {
            val failedType = FailedType.values().random()
            return FailedTipsUIModel(failedType)
        }
    }
}

class FailedTipsViewController(private val failedTipsUIModel: FailedTipsUIModel) :
    BaseViewController() {

    companion object {
        const val TAG_NAME = "failedtips"
    }

    @ViewRes(res = R.id.view_controller_failedtips_imageview)
    lateinit var failedTipsImageView: AppCompatImageView

    @ViewRes(res = R.id.view_controller_failedtips_textview)
    lateinit var failedTipsTextView: AppCompatTextView

    @ViewRes(res = R.id.view_controller_failedtips_back_to_home_button)
    lateinit var backToHomeButton: AppCompatButton

    @ViewRes(res = R.id.view_controller_titlebar)
    lateinit var titlebar: TitleBar

    init {
        tag = TAG_NAME
        presentationStyle.apply {
            animation = PresentingAnimation.BOTTOM
            overCurrentContext = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.view_controller_failedtips, container, false)
    }

    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)

        /* 返回到上页 */
        titlebar.onClickerToLeft { dismiss(animated = true) }
        /* 退出到主页 */
        backToHomeButton.setOnClickListener { dismiss(animated = true) }
        failedTipsTextView.text = activity.getString(failedTipsUIModel.failedType.tipsResId)
        failedTipsImageView.setImageResource(failedTipsUIModel.failedType.iconResId)
    }
}
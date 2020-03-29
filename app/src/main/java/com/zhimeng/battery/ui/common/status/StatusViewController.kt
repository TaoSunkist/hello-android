package com.zhimeng.battery.ui.common.status

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.zhimeng.battery.R
import com.zhimeng.battery.ui.ChargingController
import com.zhimeng.battery.ui.ChargingUIModel
import com.zhimeng.battery.ui.overdraft.OverdraftUIModel
import com.zhimeng.battery.ui.overdraft.OverdraftViewController
import com.zhimeng.battery.ui.reusable.controller.BaseViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentingAnimation
import com.zhimeng.battery.ui.reusable.viewcontroller.util.annotation.ViewRes
import com.zhimeng.battery.utilities.Dimens
import es.dmoral.toasty.Toasty

enum class Status(val tipsContent: String) {
    OVERDRAFT(
        "You have an unpaid order.\n" +
                "Please finish the previous payment to proceed.\n" +
                "Thank you."
    ),
    USING(
        "You have a in-use powerbank\n" +
                "Thank you for seleteing our service"
    ),
    NONE("")
}

data class StatusUIModel(val status: Status) {
    companion object {
        fun fake(): StatusUIModel {
            val status = when (com.zhimeng.battery.data.model.Status.values().random()) {
                com.zhimeng.battery.data.model.Status.OVERDRAFT -> Status.OVERDRAFT
                else -> Status.USING
            }
            return StatusUIModel(status = status)
        }
    }
}

class StatusViewController(val uiModel: StatusUIModel) : BaseViewController() {

    companion object {
        const val TAG_NAME = "status"
    }

    @ViewRes(res = R.id.view_controller_status_tips_content_textview)
    lateinit var tipsContentTextView: AppCompatTextView

    @ViewRes(res = R.id.view_controller_status_button)
    lateinit var browseChargingButton: AppCompatButton

    init {
        tag = TAG_NAME
        presentationStyle.apply {
            fullscreen = false
            minimumSideMargin = Dimens.marginXXLarge
            dim = true
            gravity = Gravity.CENTER
            animation = PresentingAnimation.BOTTOM
            overCurrentContext = true
            allowDismiss = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.view_controller_status, container, false)
    }

    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)
        tipsContentTextView.text = uiModel.status.tipsContent

        browseChargingButton.setOnClickListener {
            when (uiModel.status) {
                Status.OVERDRAFT -> {
                    val controller = OverdraftViewController(uiModel = OverdraftUIModel.fake())
                    present(viewController = controller, animated = true)
                }
                Status.USING -> {
                    val controller = ChargingController(uiModel = ChargingUIModel.fake())
                    present(viewController = controller, animated = true)
                }
                else -> {
                }
            }
        }
    }
}
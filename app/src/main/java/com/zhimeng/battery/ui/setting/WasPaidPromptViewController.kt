package com.zhimeng.battery.ui.setting

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhimeng.battery.R
import com.zhimeng.battery.ui.reusable.controller.BaseViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentingAnimation
import com.zhimeng.battery.utilities.Dimens

class WasPaidPromptViewController : BaseViewController() {

    companion object {
        const val TAG = "waspaidprompt"
    }

    init {
        presentationStyle.apply {
            animation = PresentingAnimation.RIGHT_TRANSLATION
            overCurrentContext = true
            fullscreen = false
            gravity = Gravity.CENTER
            allowDismiss = true
            tag = TAG
            minimumSideMargin = Dimens.marginXXLarge
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.view_controller_was_paid_prompt, container, false)
    }

    override fun viewDidDisappear(animated: Boolean) {
        super.viewDidDisappear(animated)
    }
}
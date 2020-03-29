package com.zhimeng.battery.ui.authentication

import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import com.zhimeng.battery.R
import com.zhimeng.battery.ui.reusable.controller.WebViewViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentingAnimation
import com.zhimeng.battery.utilities.Dimens
import com.zhimeng.battery.utilities.dismissKeyboard
import com.zhimeng.battery.utilities.weak

interface AgreementViewControllerDelegate {
    fun agreementViewControllerDidAgree()
}

class AgreementViewController : WebViewViewController() {

    companion object {
        const val TAG = "tagAgreementView"
    }

    var delegate: AgreementViewControllerDelegate? by weak()

    init {
        tag = TAG
        presentationStyle.apply {
            animation = PresentingAnimation.BOTTOM
            fullscreen = true
        }
    }

    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)
        fitSafeArea(contentView = view)
        view.dismissKeyboard()

        toolbar = view.findViewById<Toolbar>(R.id.common_id_toolbar).also {
            it.setNavigationIcon(R.drawable.ic_arrow_back_black)
            it.title = "用户协议"
            it.setNavigationOnClickListener {
                dismiss(animated = true)
            }
        }
        webView?.loadUrl("http://ft.zodme.com/static/agreement.html")

        val container = (view as FrameLayout)
        val agreeButton = AppCompatButton(
            ContextThemeWrapper(
                context,
                R.style.defaultGradientButtonStyle
            ),
            null,
            R.style.defaultGradientButtonStyle
        ).apply {
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, Dimens.dpToPx(50)).apply {
                gravity = Gravity.BOTTOM
                setMargins(
                    Dimens.marginMedium,
                    Dimens.marginMedium,
                    Dimens.marginMedium,
                    Dimens.marginMedium
                )
            }
            setPadding(0, Dimens.marginMedium, 0, Dimens.marginMedium)
            setOnClickListener { agreeButtonPressed() }

            gravity = Gravity.CENTER
            textSize = Dimens.fontSizeLarge
        }

        val tv = TypedValue()
        val actionBarHeight = when {
            context.theme.resolveAttribute(
                android.R.attr.actionBarSize,
                tv,
                true
            ) -> TypedValue.complexToDimensionPixelSize(tv.data, context.resources.displayMetrics)
            else -> 0
        }

        val webViewLayoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
            setMargins(0, actionBarHeight, 0, Dimens.dpToPx(50) + Dimens.marginMedium * 2)
        }
        webView?.layoutParams = webViewLayoutParams

        container.addView(agreeButton)
    }

    private fun agreeButtonPressed() {
        delegate?.agreementViewControllerDidAgree()
        dismiss(animated = true)
    }
}

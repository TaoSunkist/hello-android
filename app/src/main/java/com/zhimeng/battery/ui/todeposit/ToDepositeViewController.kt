package com.zhimeng.battery.ui.todeposit

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.zhimeng.battery.R
import com.zhimeng.battery.ui.reusable.controller.BaseViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentingAnimation
import com.zhimeng.battery.ui.reusable.viewcontroller.util.annotation.ViewRes
import com.zhimeng.battery.ui.reusable.views.TitleBar
import com.zhimeng.battery.utilities.Dimens
import com.zhimeng.battery.utilities.SpannableStringBuilderCompat
import android.text.style.RelativeSizeSpan
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import com.zhimeng.battery.app.MainActivity
import com.zhimeng.battery.ui.payment.PaymentSelectionViewController
import com.zhimeng.battery.utilities.Colors

class ToDepositeViewController : BaseViewController() {

    companion object {
        const val TAG = "waspaid"
    }

    @ViewRes(res = R.id.view_controller_was_paid_body_container)
    lateinit var bodyContainer: LinearLayoutCompat

    @ViewRes(res = R.id.view_controller_titlebar)
    lateinit var titlebar: TitleBar

    @ViewRes(res = R.id.view_controller_was_paid_agreement_textview)
    lateinit var agreementTextView: AppCompatTextView

    @ViewRes(res = R.id.view_controller_was_paid_to_lease_button)
    lateinit var toLeaseButton: AppCompatButton

    private val itemsStringResIds = arrayOf(
        R.string.address,
        R.string.three_line,
        R.string.coin,
        R.string.battery_box
    )
    private val itemsDrawableStartResIds = arrayOf(
        R.drawable.ic_address,
        R.drawable.ic_three_line,
        R.drawable.ic_coin,
        R.drawable.ic_battery_box
    )

    init {
        presentationStyle.apply {
            animation = PresentingAnimation.RIGHT_TRANSLATION
            tag = TAG
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.view_controller_to_deposit, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)

        titlebar.onClickerToLeft { dismiss(animated = true) }
        toLeaseButton.setOnClickListener { toPay() }
        val agreementTextViewSpan =
            SpannableStringBuilderCompat.singleSpan(agreementTextView.text, Any())
        agreementTextViewSpan.setSpan(
            ForegroundColorSpan(Colors.primaryColor),
            agreementTextView.text.indexOf("《"),
            agreementTextView.text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        agreementTextView.text = agreementTextViewSpan

        for (i in 0..3) {
            val itemTextView = AppCompatTextView(context).apply {
                this.layoutParams = LinearLayoutCompat.LayoutParams(
                    MATCH_PARENT,
                    WRAP_CONTENT
                )
                compoundDrawablePadding = Dimens.marginLarge
                gravity = Gravity.START
                textSize = 16f
                text = context.getText(itemsStringResIds[i])
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.BLACK)
                setPadding(
                    Dimens.marginXLarge,
                    if (i == 0) Dimens.marginXLarge else Dimens.marginLarge,
                    Dimens.marginLarge,
                    Dimens.marginLarge
                )
                bodyContainer.addView(this)
            }

            val spannableString = SpannableStringBuilderCompat.singleSpan(itemTextView.text, Any())
            val itemTextLength = itemTextView.text.length

            itemTextView.setCompoundDrawablesWithIntrinsicBounds(
                itemsDrawableStartResIds[i], 0, 0, 0
            )
            /*subtitle spannable*/
            if (itemTextView.text.contains("\n")) {
                val titleIndex = itemTextView.text.indexOf("\n")
                spannableString.setSpan(
                    RelativeSizeSpan(0.72f),
                    titleIndex,
                    itemTextLength,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    ForegroundColorSpan(Color.GRAY),
                    titleIndex,
                    itemTextLength,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    StyleSpan(Typeface.NORMAL),
                    titleIndex,
                    itemTextLength,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            itemTextView.text = spannableString
        }
    }

    private fun toPay() {
        val controller = PaymentSelectionViewController()
        present(viewController = controller, animated = true)
    }
}
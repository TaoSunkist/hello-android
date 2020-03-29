package com.zhimeng.battery.ui.waspaid

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
import android.text.style.RelativeSizeSpan
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import com.zhimeng.battery.app.MainActivity
import com.zhimeng.battery.data.service.StationService
import com.zhimeng.battery.data.service.UserService
import com.zhimeng.battery.ui.ChargingController
import com.zhimeng.battery.ui.ChargingUIModel
import com.zhimeng.battery.ui.common.LoadingCoverWrapperDelegate
import com.zhimeng.battery.ui.overdraft.OverdraftUIModel
import com.zhimeng.battery.ui.overdraft.OverdraftViewController
import com.zhimeng.battery.utilities.*
import es.dmoral.toasty.Toasty
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import java.util.concurrent.TimeUnit

interface WasPaidViewControllerDelegate {
    fun wasPaidViewControllerDelegateDidBorrow()
}

class WasPaidViewController(private val boxId: String) : BaseViewController(),
    LoadingCoverWrapperDelegate {

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
        return inflater.inflate(R.layout.view_controller_was_paid, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)

        titlebar.onClickerToLeft { dismiss(animated = true) }
        toLeaseButton.setOnClickListener { toLease() }
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

    private fun toLease() {
//        (activity as MainActivity).showLoadingCover(this)
        showProgressDialog()
        StationService.shared.borrowing(boxId = boxId)
            .observeOnMainThread(
                onSuccess = {
                    /*租借成功*/
                    queryUserStatus()
                },
                onError = { dismissProgressDialog() },
                onTerminate = { //                    (activity as MainActivity).hideLoadingCover()
                }).addTo(compositeDisposable = compositeDisposable)
    }

    private fun queryUserStatus() {
        UserService.shared.usersStatus()
            .observeOnMainThread(onSuccess = {
                val controller = ChargingController(uiModel = ChargingUIModel.init(it))
                present(viewController = controller, animated = true)
                dismissProgressDialog()
            }, onError = {
                Toasty.normal(context, it.localizedMessage ?: "unknown falied.").show()
                dismissProgressDialog()
            }, onTerminate = {
            }).addTo(compositeDisposable = compositeDisposable)
    }


    override fun loadingCoverWrapperDelegateDidArtificialShutdown() {
//        (activity as MainActivity).hideLoadingCover()
    }

    override fun loadingCoverWrapperDelegateDidLease() {
        val controller = ChargingController(uiModel = ChargingUIModel.fake())
        present(viewController = controller, animated = true)
    }
}
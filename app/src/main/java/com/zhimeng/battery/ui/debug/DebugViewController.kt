package com.zhimeng.battery.ui.debug

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.PublishRelay
import com.zhimeng.battery.app.MainActivity
import com.zhimeng.battery.ui.ChargingController
import com.zhimeng.battery.ui.ChargingUIModel
import com.zhimeng.battery.ui.common.LoadingCoverWrapperDelegate
import com.zhimeng.battery.ui.common.status.*
import com.zhimeng.battery.ui.home.HomeViewController
import com.zhimeng.battery.ui.overdraft.OverdraftUIModel
import com.zhimeng.battery.ui.overdraft.OverdraftViewController
import com.zhimeng.battery.ui.payment.PaymentSelectionViewController
import com.zhimeng.battery.ui.reusable.controller.BaseViewController
import com.zhimeng.battery.ui.scanqrcode.ScanQRCodeViewController
import com.zhimeng.battery.ui.setting.WasPaidPromptViewController
import com.zhimeng.battery.ui.todeposit.ToDepositeViewController
import com.zhimeng.battery.utilities.Dimens
import com.zhimeng.battery.utilities.MainThread
import com.zhimeng.battery.utilities.PermissionRequester
import com.zhimeng.battery.utilities.observeOnMainThread
import es.dmoral.toasty.Toasty
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import java.util.concurrent.TimeUnit

class DebugViewController : BaseViewController(), LoadingCoverWrapperDelegate {

    init {
        tag = "debug"
    }

    private class DebugCellViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        AppCompatTextView(parent.context)
    ) {
        init {
            itemView.setPadding(
                Dimens.marginLarge,
                Dimens.marginLarge,
                Dimens.marginLarge,
                Dimens.marginLarge
            )
        }

        fun setTitle(title: String) {
            (itemView as? AppCompatTextView)?.text = title
        }
    }

    private class ListAdapter : RecyclerView.Adapter<DebugCellViewHolder>() {

        var items = listOf<String>()
            set(value) {
                field = value
                notifyDataSetChanged()
            }
        var selectionRelay = PublishRelay.create<Int>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebugCellViewHolder {
            return DebugCellViewHolder(parent = parent)
        }

        override fun getItemCount(): Int {
            return items.count()
        }

        override fun onBindViewHolder(holder: DebugCellViewHolder, position: Int) {
            holder.setTitle(items[position])
            holder.itemView.setOnClickListener {
                selectionRelay.accept(position)
            }
        }
    }

    private val recyclerView: RecyclerView by lazy {
        RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.WHITE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return recyclerView
    }

    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)

        recyclerView.adapter = ListAdapter().apply {
            items = listOf(
                "Authentication", "Home", "Status",
                "FailedTips", "WasPaidPrompt",
                "Payment", "WasPaid", "Overdraft",
                "LoadingCover", "Charging", "ScanQRCode"
            )
            selectionRelay.observeOn(MainThread).subscribe {

                when (it) {
                    0 -> {
                        dismiss(animated = true)
//                        val controller = AuthenticationViewController()
//                        present(viewController = controller, animated = true)
                    }
                    1 -> {
                        val controller = HomeViewController()
                        present(viewController = controller, animated = true)
                    }
                    2 -> {
                        val uiModel = StatusUIModel.fake()
                        val controller = StatusViewController(uiModel)
                        present(viewController = controller, animated = true)
                    }
                    3 -> {
                        val controller = FailedTipsViewController(FailedTipsUIModel.fake())
                        present(viewController = controller, animated = true)
                    }
                    4 -> {
                        val controller = WasPaidPromptViewController()
                        present(viewController = controller, animated = true)
                    }
                    5 -> {
                        val controller = PaymentSelectionViewController()
                        present(viewController = controller, animated = true)
                    }
                    6 -> {
                        val controller = ToDepositeViewController()
                        present(viewController = controller, animated = true)
                    }
                    7 -> {
                        val controller = OverdraftViewController(uiModel = OverdraftUIModel.fake())
                        present(viewController = controller, animated = true)
                    }
                    8 -> {
                        (activity as MainActivity).showLoadingCover(this@DebugViewController)
                        Observable.interval(1, TimeUnit.SECONDS).subscribeOn(MainThread)
                            .subscribe { period ->
                                if (period >= 3) {
                                    (activity as MainActivity).hideLoadingCover()
                                }
                            }.addTo(compositeDisposable)
                    }
                    9 -> {
                        loadingCoverWrapperDelegateDidLease()
                    }
                    10 -> {
                        PermissionRequester.getCamera(activity).observeOnMainThread(onSuccess = {
                            val controller = ScanQRCodeViewController()
                            present(viewController = controller, animated = true)
                        }, onError = {
                            Toasty.normal(context, "权限获取失败, 请授权相机使用权限").show()
                        }).addTo(compositeDisposable = compositeDisposable)
                    }
                    else -> {
                    }
                }

            }.addTo(compositeDisposable = compositeDisposable)
        }
    }

    override fun loadingCoverWrapperDelegateDidArtificialShutdown() {
        (activity as MainActivity).hideLoadingCover()
    }

    override fun loadingCoverWrapperDelegateDidLease() {
        val controller = ChargingController(uiModel = ChargingUIModel.fake())
        present(viewController = controller, animated = true)
    }
}
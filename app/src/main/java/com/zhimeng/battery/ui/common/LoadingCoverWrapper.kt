package com.zhimeng.battery.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhimeng.battery.R
import com.zhimeng.battery.ui.common.indicatorprogressbar.IndicatorProgressBar
import com.zhimeng.battery.utilities.weak
import io.reactivex.disposables.CompositeDisposable
import java.util.*

interface LoadingCoverWrapperDelegate {
    fun loadingCoverWrapperDelegateDidArtificialShutdown()
    fun loadingCoverWrapperDelegateDidLease()
}

class LoadingCoverWrapper(parent: ViewGroup) {

    private var delegate: LoadingCoverWrapperDelegate? by weak()
    val view: View =
        LayoutInflater.from(parent.context).inflate(R.layout.view_loading_cover, parent, false)
    private val backgroundView: View = view.findViewById(R.id.view_loading_cover_background)
    private val contentView: View = view.findViewById(R.id.view_loading_cover_content)
    private val indicatorProgressBar: IndicatorProgressBar =
        view.findViewById(R.id.view_loading_cover_content_indicator_progressbar)
    private lateinit var compositeDisposable: CompositeDisposable
    private var period = 25.toLong()
    private var timer: Timer? = null

    fun showAsLoading(delegate: LoadingCoverWrapperDelegate? = null) {
        delegate?.let { this.delegate = it }
        if (timer != null) return
        backgroundView.visibility = View.GONE
        backgroundView.isClickable = false
        contentView.visibility = View.VISIBLE
        view.visibility = View.VISIBLE
        compositeDisposable = CompositeDisposable()
        val firstPartProgress = listOf(0.4f, 0.6f, 0.8f).random()

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                view.post {
                    if (indicatorProgressBar.progress >= 1f) {
                        view.visibility = View.GONE
                        indicatorProgressBar.progress = 0.00f
                        period = 25.toLong()
                        timer?.cancel()
                        timer = null
                    } else if (indicatorProgressBar.progress > firstPartProgress) {
                        if (period == 5.toLong()) {
                            indicatorProgressBar.progress += 0.015f
                        }
                    } else {
                        indicatorProgressBar.progress += 0.01f
                    }
                }
            }
        }, 0, period)
    }

    fun hideAsLoading() {
        period = 5.toLong()
        delegate = null
    }
}
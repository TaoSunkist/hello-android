package com.zhimeng.battery.ui.reusable.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.Toolbar
import com.zhimeng.battery.R

open class WebViewViewController : BaseViewController() {

    var toolbar: Toolbar? = null
    var webView: WebView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.view_controller_web_view, container, false)
    }

    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)
        setupToolbar(view)
        webView = view.findViewById(R.id.fragment_agreement_webview)
        webView?.webViewClient = WebViewClient()
    }

    private fun setupToolbar(view: View) {
        toolbar = view.findViewById<Toolbar>(R.id.common_id_toolbar).also {
            it.setNavigationIcon(R.drawable.ic_arrow_back_black)
            it.setNavigationOnClickListener {
                dismiss(animated = true)
            }
        }
    }
}
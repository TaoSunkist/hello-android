package com.zhimeng.battery.ui.authentication

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.zhimeng.battery.R
import com.zhimeng.battery.network.BatteryApi
import com.zhimeng.battery.network.availableServers
import com.zhimeng.battery.ui.reusable.controller.BaseViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentingAnimation
import com.zhimeng.battery.ui.reusable.viewcontroller.util.annotation.ViewRes

class ServerSelectionViewController : BaseViewController() {

    companion object {
        const val TAG = "serverSelection"
    }

    init {
        tag = TAG
        presentationStyle.apply {
            allowTapOutsideToDismiss = true
            fullscreen = false
            gravity = Gravity.CENTER
            overCurrentContext = true
            animation = PresentingAnimation.BOTTOM
        }
    }

    @ViewRes(res = R.id.view_controller_server_selection_list)
    private lateinit var serverListView: ViewGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.view_controller_server_selection, container, false)
    }

    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)

        availableServers.asSequence().forEach { entry ->
            val key = entry.key
            val server = entry.value

            AppCompatButton(context).also {
                if (key == BatteryApi.sharedInstance.selectedServerKey.value) {
                    it.setBackgroundResource(R.drawable.bg_button_stroke_rounded_50dp)
                    it.setTextColor(Color.RED)
                } else {
                    it.setBackgroundResource(R.drawable.bg_button_stroke_rounded_50dp)
                    it.setTextColor(Color.BLACK)
                }
                it.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
                it.text = server.name
                serverListView.addView(it)

                it.setOnClickListener {
                    BatteryApi.sharedInstance.switchToServer(context = context,
                            serverInfo = server)
                    dismiss(animated = true)
                }
            }
        }
    }
}
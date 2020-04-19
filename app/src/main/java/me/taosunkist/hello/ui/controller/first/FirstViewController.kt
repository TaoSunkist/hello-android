package me.taosunkist.hello.ui.controller.first

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.taosunkist.hello.R
import me.taosunkist.hello.ui.reusable.viewcontroller.controller.BaseViewController

class FirstViewController : BaseViewController() {

	init {
		tag = "home"
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
		return inflater.inflate(R.layout.view_controller_home, container, false)
	}
}
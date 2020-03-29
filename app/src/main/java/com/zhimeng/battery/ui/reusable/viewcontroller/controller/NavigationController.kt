package com.zhimeng.battery.ui.reusable.viewcontroller.controller

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhimeng.battery.ui.reusable.viewcontroller.host.ControllerHost

open class NavigationController(root: ViewController) : ViewController() {

    protected var controllerHost: ControllerHost? = null
    var root = root
    private var pendingRoot: ViewController? = root

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        controllerHost = ControllerHost(activity = activity)
        controllerHost?.setBackgroundColor(Color.WHITE)
        return controllerHost!!
    }

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)

        if (pendingRoot != null) {
            pendingRoot!!.navigationController = this
            controllerHost!!.push(
                viewController = pendingRoot!!,
                activity = activity,
                animated = false,
                completion = null
            )
            pendingRoot = null
        }
    }

    override fun doDestroyView(container: ViewGroup) {
        controllerHost?.popAll()
        controllerHost?.onDestroy()
        root.navigationController = null
        super.doDestroyView(container)
    }

    fun push(viewController: ViewController, animated: Boolean) {
        viewController.navigationController = this
        controllerHost?.push(
            viewController = viewController,
            activity = activity,
            animated = animated,
            completion = null
        )
    }

    fun popViewController(animated: Boolean) {
        controllerHost?.pop(animated, completion = null)
    }

    fun popToRoot() {
        controllerHost?.popToRoot()
    }

    override fun onBackPressed(): Boolean {
        return controllerHost?.onBackPressed() ?: false
    }
}
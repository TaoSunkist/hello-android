package com.zhimeng.battery.ui.reusable.viewcontroller.window

import com.zhimeng.battery.ui.reusable.activity.BaseActivity
import com.zhimeng.battery.ui.reusable.viewcontroller.controller.ViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.host.ControllerHost
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentingAnimation

class Window(activity: BaseActivity) : ControllerHost(activity = activity) {

    fun setRootViewController(
        controller: ViewController
    ) {
        if (activity == null) {
            return
        }
        if (topViewController == null) {
            push(
                viewController = controller,
                activity = activity!!,
                animated = false,
                completion = null
            )
        } else if (topViewController?.tag != controller.tag) {
            popAllButTop()
            topViewController?.presentationStyle?.animation = PresentingAnimation.BOTTOM
            addBottom(viewController = controller, activity = activity!!)
            pop(animated = true, completion = null)
        }
    }
}
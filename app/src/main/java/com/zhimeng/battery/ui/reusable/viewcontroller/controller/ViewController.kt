package com.zhimeng.battery.ui.reusable.viewcontroller.controller

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhimeng.battery.ui.reusable.activity.BaseActivity
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentationStyle
import com.zhimeng.battery.ui.reusable.viewcontroller.util.annotation.ViewRes
import com.zhimeng.battery.utilities.weak
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

abstract class ViewController {

    companion object {
        var verbose: Boolean = true
    }

    var navigationController: NavigationController? by weak()

    private var _view: View? = null
    val view: View
        get() = _view!!

    private var _activity: BaseActivity? by weak()
    val activity: BaseActivity
        get() = _activity!!
    val context: Context
        get() = _activity!!

    private var _isVisible: Boolean = false
    val isVisible: Boolean
        get() = _isVisible

    var presentationStyle = PresentationStyle()
    var tag: String? = null

    val res: Resources
        get() = _activity!!.resources

    /* Child must override */

    abstract fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View

    open fun viewDidLoad(view: View) {
        if (verbose) {
            Log.i("oneHook", "${javaClass.simpleName} ($tag) VIEW DID LOAD")
        }
        processViewResAnnotations()
    }

    private fun processViewResAnnotations() {
        for (field in this::class.memberProperties) {
            if (field is KMutableProperty<*>) {
                field.findAnnotation<ViewRes>()?.let {
                    field.isAccessible = true
                    field.setter.call(this, this.view.findViewById(it.res))
                }
            }
        }
    }

    open fun viewWillUnload(view: View) {
        if (verbose) {
            Log.i("oneHook", "${javaClass.simpleName} ($tag) VIEW WILL UNLOAD")
        }
    }

    open fun viewWillAppear(animated: Boolean) {
        if (verbose) {
            Log.i(
                "oneHook",
                "${javaClass.simpleName} ($tag) VIEW WILL APPEAR (${view.measuredWidth}, ${view.measuredHeight})"
            )
        }
    }

    open fun viewDidAppear(animated: Boolean) {
        _isVisible = true
        if (verbose) {
            Log.i(
                "oneHook",
                "${javaClass.simpleName} ($tag) VIEW DID APPEAR (${view.measuredWidth}, ${view.measuredHeight})"
            )
        }
    }

    open fun viewWillDisappear(animated: Boolean) {
        if (verbose) {
            Log.i("oneHook", "${javaClass.simpleName} ($tag) VIEW WILL DISAPPEAR")
        }
    }

    open fun viewDidDisappear(animated: Boolean) {
        _isVisible = false
        if (verbose) {
            Log.i("oneHook", "${javaClass.simpleName} ($tag) VIEW DID DISAPPEAR")
        }
    }

    /* Internal Methods */

    open fun doCreateView(container: ViewGroup, activity: BaseActivity) {
        _activity = activity
        _view = onCreateView(LayoutInflater.from(container.context), container)
        viewDidLoad(view)
        if (verbose) {
            Log.i("oneHook", "${javaClass.simpleName} ($tag) CREATE VIEW")
        }
    }

    open fun doDestroyView(container: ViewGroup) {
        this.viewWillUnload(view)
        container.removeView(view)
        _activity = null
        _view = null
        navigationController = null

        if (verbose) {
            Log.i("oneHook", "${javaClass.simpleName} ($tag) DESTROY VIEW")
        }
    }

    /**
     * If return true, means view controller would like to handle this
     * by itself.
     */
    open fun onBackPressed(): Boolean {
        return false
    }

    /* Public methods */

    open fun present(
        viewController: ViewController,
        animated: Boolean,
        completion: (() -> Unit)? = null
    ) {
        activity.controllerWindow.push(
            viewController = viewController,
            activity = activity,
            animated = animated,
            completion = completion
        )
    }

    open fun dismiss(animated: Boolean, completion: (() -> Unit)? = null) {
        activity.controllerWindow.pop(animated = animated, completion = completion)
    }
}
package com.zhimeng.battery.ui.reusable.activity

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.marginTop
import com.zhimeng.battery.R
import com.zhimeng.battery.app.BatteryApplication
import com.zhimeng.battery.ui.reusable.viewcontroller.window.Window
import com.zhimeng.battery.ui.reusable.views.ProgressDialogWrapper
import com.zhimeng.battery.utilities.Dimens

open class BaseActivity : AppCompatActivity() {

    open val viewRes: Int
        get() = R.layout.activity_base

    private lateinit var progressDialog: ProgressDialogWrapper
    private var disableBackButton: Boolean = false

    val container: ViewGroup
        get() = findViewById<FrameLayout>(R.id.activity_main_container)
    val controllerWindow: Window by lazy {
        val window = Window(activity = this)
        container.addView(window)
        window
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*supportRequestWindowFeature(android.view.Window.FEATURE_NO_TITLE)*/

        setContentView(viewRes)
        observeSafeArea()
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            adjustTopPadding()
        }

        container.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        progressDialog = ProgressDialogWrapper(parent = container)
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    private fun getStatusBarHeight(): Int {
        var statusBarHeight = 0;
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    private fun getNavigationBarHeight(): Int {
        var navigationBarHeight = 0;
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        return navigationBarHeight;
    }

    @TargetApi(28)
    private fun adjustTopPadding() {
        controllerWindow.setOnApplyWindowInsetsListener { _, insets ->
            val rect = Rect()
            rect.top = insets.displayCutout?.safeInsetTop ?: 0
            rect.left = insets.displayCutout?.safeInsetLeft ?: 0
            rect.right = insets.displayCutout?.safeInsetRight ?: 0
            rect.bottom = insets.displayCutout?.safeInsetBottom ?: 0
            rect.bottom = getNavigationBarHeight()
            Dimens.safeArea = rect
            insets
        }
    }

    private fun observeSafeArea() {
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            val rect = Rect()
            rect.top = insets.stableInsetTop
            rect.left = insets.stableInsetLeft
            rect.right = insets.stableInsetRight
            rect.bottom = insets.stableInsetBottom
            if (Dimens.safeArea != rect) {
                Dimens.safeArea = rect
            }
            /* 假装我们已经拿到了top 和bottom，其实我们自己根据需要加，如果不清0就会多
               但是同时我们又想靠这个拿键盘的高度...所以.... */
            insets.replaceSystemWindowInsets(
                insets.stableInsetLeft,
                0,
                insets.stableInsetRight,
                0
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controllerWindow.onDestroy()
    }

    override fun onBackPressed() {
        if (disableBackButton) {
            return
        }
        if (!controllerWindow.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        BatteryApplication.activeActivityCount
            .accept((BatteryApplication.activeActivityCount.value ?: 0) + 1)
    }

    override fun onStop() {
        super.onStop()
        BatteryApplication.activeActivityCount
            .accept((BatteryApplication.activeActivityCount.value ?: 1) - 1)
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.anim_slide_up, R.anim.anim_none)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.anim_none, R.anim.anim_slide_down)
    }

    fun showProgressDialog(
        messageRes: Int,
        animated: Boolean = true,
        completion: (() -> Unit)? = null
    ) {
        showProgressDialog(
            message = getString(messageRes),
            animated = animated,
            completion = completion
        )
    }

    fun showProgressDialog(
        message: String,
        animated: Boolean = true,
        completion: (() -> Unit)? = null
    ) {
        disableBackButton = true
        progressDialog.progressText.text = message
        if (progressDialog.view.parent == null) {
            container.addView(progressDialog.view)
            progressDialog.show(animated = animated, completion = {
                completion?.invoke()
            })
        }
    }

    fun dismissProgressDialog(animated: Boolean = true, completion: (() -> Unit)? = null) {
        if (progressDialog.view.parent != null) {
            progressDialog.hide(animated = animated, completion = {
                container.removeView(progressDialog.view)
                completion?.invoke()
                disableBackButton = false
            })
        }
    }
}
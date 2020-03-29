package com.zhimeng.battery.app

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import com.zhimeng.battery.R
import com.zhimeng.battery.data.UserStore
import com.zhimeng.battery.ui.authentication.AuthenticationViewController
import com.zhimeng.battery.ui.common.LoadingCoverWrapper
import com.zhimeng.battery.ui.common.LoadingCoverWrapperDelegate
import com.zhimeng.battery.ui.home.HomeViewController
import com.zhimeng.battery.ui.reusable.activity.BaseActivity
import com.zhimeng.battery.ui.reusable.viewcontroller.controller.NavigationController
import com.zhimeng.battery.utilities.Dimens
import com.zhimeng.battery.utilities.MainThread
import com.zhimeng.battery.utilities.PermissionGranter
import com.zhimeng.battery.utilities.weak
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class MainActivity : BaseActivity(), PermissionGranter {

    private val compositeDisposable = CompositeDisposable()
    private var navigationController: NavigationController? by weak()
    private var authenticationViewController: AuthenticationViewController? by weak()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dimens.safeAreaRelay.firstElement().observeOn(MainThread)
            .subscribe {
                navigationController = NavigationController(root = HomeViewController()).apply {
                    controllerWindow.setRootViewController(this)
                }
                /* 我们的Root永远都是Home，也就是说HomeVC要有能力处理用户没有登录的情况
                   这样的好处是如果有朝一日我们允许可以用户未登录状态下也能先看看内容，现在
                   如果用户没有登录，我们就直接盖上AuthenticationViewController */
                if (!UserStore.shared.isAuthenticated) {
                    launchAuthenticationFlow(animated = false)
                }
                observeUser()
                intent?.let { processIntent(intent) }
            }.addTo(compositeDisposable = compositeDisposable)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { processIntent(intent) }
    }

    private fun processIntent(intent: Intent) {}

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        BatteryApplication.appCompositeDisposable.clear()
    }

    private fun observeUser() {
        UserStore.shared.userObjectRelay
            .filter { it.timestamp > 0 }
            .observeOn(MainThread)
            .subscribe {
                if (it.isAuthenticated) {
                    dismissAuthenticationFlow()
                } else {
                    launchAuthenticationFlow(animated = true)
                }
            }.addTo(compositeDisposable = compositeDisposable)
    }

    private val loadingCoverViewWrapper: LoadingCoverWrapper by lazy {
        val parent: FrameLayout = findViewById(R.id.activity_main_container)
        val wrapper = LoadingCoverWrapper(parent = parent)
        parent.addView(wrapper.view)
        wrapper
    }

    fun showLoadingCover(delegate: LoadingCoverWrapperDelegate? = null) {
        loadingCoverViewWrapper.showAsLoading()
    }

    fun hideLoadingCover() {
        loadingCoverViewWrapper.hideAsLoading()
    }

    private fun launchAuthenticationFlow(animated: Boolean) {

        if (authenticationViewController != null) {
            return
        }

        val controller = AuthenticationViewController()
        controllerWindow.push(
            viewController = controller,
            activity = this,
            animated = animated,
            completion = {
                /* 确保让Home回到Root */
                navigationController?.popToRoot()
            }
        )
        authenticationViewController = controller
    }

    private fun dismissAuthenticationFlow() {
        if (authenticationViewController != null) {
            controllerWindow.pop(animated = true, completion = {})
            authenticationViewController = null
        }
    }

    /* Permission Granter */

    private var permissionHandler: Function1<Int, Unit>? = null
    private var permissionRequestCode = 217

    override fun requestPermission(permission: String, resultHandler: Function1<Int, Unit>) {
        if (this.permissionHandler != null) throw IllegalStateException("Cannot request another while pending")
        this.permissionHandler = resultHandler
        ActivityCompat.requestPermissions(
            this, arrayOf(permission),
            this.permissionRequestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (this.permissionHandler != null && requestCode == this.permissionRequestCode &&
            grantResults.size == 1
        ) {
            this.permissionHandler!!.invoke(grantResults[0])
            this.permissionHandler = null
            this.permissionRequestCode++
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /* 别删除这个,Umeng分享需要. */
    }
}

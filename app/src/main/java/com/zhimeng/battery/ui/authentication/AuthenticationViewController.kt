package com.zhimeng.battery.ui.authentication

import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.zhimeng.battery.BuildConfig
import com.zhimeng.battery.R
import com.zhimeng.battery.data.UserObjectEventType
import com.zhimeng.battery.data.UserStore
import com.zhimeng.battery.data.model.ExampleLoginInfo
import com.zhimeng.battery.data.service.UserService
import com.zhimeng.battery.network.model.UserResponse
import com.zhimeng.battery.ui.debug.DebugViewController
import com.zhimeng.battery.ui.reusable.controller.BaseViewController
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentationStyle
import com.zhimeng.battery.ui.reusable.viewcontroller.presentation.PresentingAnimation
import com.zhimeng.battery.ui.reusable.viewcontroller.util.annotation.ViewRes
import com.zhimeng.battery.utilities.observeOnMainThread
import es.dmoral.toasty.Toasty
import io.reactivex.rxkotlin.addTo
import org.angmarch.views.*

class AuthenticationViewController : BaseViewController(), AgreementViewControllerDelegate {

    companion object {
        const val TAG_AUTHENTICATION = "auth"
    }


    @ViewRes(res = R.id.view_controller_authentication_logo_imageview)
    lateinit var logoImageView: View

    @ViewRes(res = R.id.view_controller_authentication_nice_spinner)
    lateinit var niceSpinner: NiceSpinner

    @ViewRes(res = R.id.view_controller_authentication_version_textview)
    lateinit var versionTextView: AppCompatTextView

    @ViewRes(res = R.id.view_controller_authentication_login_button)
    lateinit var loginButton: AppCompatButton

    init {
        tag = TAG_AUTHENTICATION
        presentationStyle = PresentationStyle(
            animation = PresentingAnimation.BOTTOM,
            dim = false,
            fullscreen = false
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.view_controller_authentication, container, false)
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
            enableLoginButton()
    }

    private fun enableLoginButton() {
        loginButton.isEnabled = niceSpinner.text.isNotEmpty()
                && niceSpinner.text.length > 8
    }

    override fun viewDidLoad(view: View) {
        super.viewDidLoad(view)

        niceSpinner.addTextChangedListener(textWatcher)
        loginButton.setOnClickListener { /*debugLoginButton()*/loginButtonPressed() }
        setupDebugButtons()
    }

    override fun onBackPressed(): Boolean {
        /* 不允许go back */
        return true
    }

    private fun setupDebugButtons() {

        if (BuildConfig.DEBUGGING_MODE) {
            logoImageView.setOnClickListener {
                val controller = DebugViewController()
                present(viewController = controller, animated = true)
            }

            val dataset: List<ExampleLoginInfo> = UserService.shared.fakeExampleLoginInfo()
            val spinnerTextFormatter =
                SpinnerTextFormatter<ExampleLoginInfo> { item -> SpannableString("${item.userResponse.user.cellphone} ${item.userStatus.status?.tag}") }

            niceSpinner.setSpinnerTextFormatter(spinnerTextFormatter)
            niceSpinner.setSelectedTextFormatter(spinnerTextFormatter)
            niceSpinner.setOnSpinnerItemSelectedListener { parent, _, position, _ ->
                val item = (niceSpinner.getItemAtPosition(position) as ExampleLoginInfo)
                val spannableString =
                    SpannableString("${item.userResponse.user.cellphone} ${item.userStatus.status?.tag}")
                parent.text = spannableString
            }
            niceSpinner.attachDataSource(dataset)
        }
    }

    var cellphone: String? = null
    private fun loginButtonPressed() {

        val exampleLoginInfo = (niceSpinner.selectedItem as ExampleLoginInfo)
        cellphone = exampleLoginInfo.userResponse.user.cellphone
        showProgressDialog("")
        UserService.shared.login(
            cellphone = cellphone ?: ""
        ).observeOnMainThread(onSuccess = {
            /* TODO please delete this */
            onUserAuthenticated(exampleLoginInfo.userResponse)
        }, onError = {
            Toasty.normal(
                context,
                it.localizedMessage ?: context.getString(R.string.unkonwn_failed_tip)
            ).show()
        }, onTerminate = {
            dismissProgressDialog()
        }).addTo(compositeDisposable)
    }

    /* TODO 需要缓存嘛? */
    private fun onUserAuthenticated(response: UserResponse) {
        UserStore.shared.updateUser(userResponse = response, type = UserObjectEventType.LOGIN)
//        logoImageView.performClick()
    }

    override fun agreementViewControllerDidAgree() {
    }
}
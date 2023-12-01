package com.tfl.vguardrishta.ui.components.logIn

import android.app.Activity
import android.os.Build
import android.util.Log
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.addFragment
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.replaceFragment
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.forgotPassword.ForgotPasswordFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.login.FirstStartFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.login.LoginFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.loginWithOtp.LoginWithOtpFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.otp.OtpVerifyFragment
import com.tfl.vguardrishta.ui.components.vguard.home.RishtaHomeActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LogInActivity : BaseActivity<LogInContract.View, LogInContract.Presenter>(),
    LogInContract.View {

    @Inject
    lateinit var logInPresenter: LogInPresenter
    private lateinit var progress: Progress

    override fun initPresenter() = logInPresenter

    override fun injectDependencies() = getApplicationComponent().inject(this)

    override fun getLayoutResId() = R.layout.activity_login

    override fun initUI() {

        supportActionBar!!.hide()
        progress = Progress(this, R.string.please_wait)

        if (Build.VERSION.SDK_INT >= 23) {
            try {
                val permissionUtils = PermissionUtils(this)
                permissionUtils.requestForAllMandatoryPermissions()
            } catch (e: Exception) {
            }
        }
        val retLogin = intent.getBooleanExtra("retailerLogin", false)
        if (retLogin) {
            replaceFragment(LoginFragment(), R.id.content, "")
        } else {
            replaceFragment(FirstStartFragment(), R.id.content, "")
        }
    }

    override fun showProgress() = progress.show()

    override fun getActivity(): Activity = this

    override fun stopProgress() = progress.dismiss()

    override fun showToast(toast: String) = AppUtils.showToast(this, toast)

    override fun finishView() = finish()

    override fun errorEnterUserName() {

    }

    override fun errorEnterPassword() {
        etPassword.error = getString(R.string.enter_password)
        etPassword.requestFocus()
    }


    override fun onResume() {
//        runOnUiThread {
//            logInPresenter.getAppVersion()
//        }
        super.onResume()
    }

    override fun navigateToHome() {
        launchActivity<RishtaHomeActivity> { }
        finish()
    }

    override fun redirectToPlayStore() {
       // AppUtils.redirectToPlayStore(this)
    }

    override fun navigateToChangePassword() {

    }

    fun navigateToLoginFragment() {
        addFragment(LoginFragment(), R.id.content)

        // enabling language change to retailer
        /*
        if (CacheUtils.getSelectedLanguage() != "en" && CacheUtils.getRishtaUserType() == Constants.RET_USER_TYPE) {
            CacheUtils.setSelectedLanguage("en")
            finish()
            launchActivity<LogInActivity> {
                putExtra("retailerLogin",true)
            }
        }
         */
    }

    fun navigateToForgotPassword() {
        addFragment(ForgotPasswordFragment(), R.id.content)
    }

    fun navigateToEnterLoginOtp(loginWithOtpUserName: String) {
        val otpVerifyFragment = OtpVerifyFragment()
        intent.putExtra(Constants.IS_LOGIN_WITH_OTP, true)
        intent.putExtra(Constants.LOGIN_OTP_USER_NAME, loginWithOtpUserName)
        addFragment(otpVerifyFragment, R.id.content)
    }

    fun navigateToLoginWithOtp() {
        addFragment(LoginWithOtpFragment(), R.id.content)
    }
}

package com.tfl.vguardrishta.ui.components.vguard.fragment.login

import android.view.View
import android.widget.AdapterView
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.BuildConfig
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.extensions.visible
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.logIn.LogInActivity
import com.tfl.vguardrishta.ui.components.reupdateKyc.ReUpdateKycActivity
import com.tfl.vguardrishta.ui.components.vguard.fragment.newuser.NewUserRegistrationActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment : BaseFragment<LogInFragmentContract.View, LogInFragmentContract.Presenter>(),
    LogInFragmentContract.View, View.OnClickListener {
    private lateinit var progress: Progress
    private var userTouch = false

    @Inject
    lateinit var logInFragmentPresenter: LogInFragmentPresenter


    override fun initPresenter(): LogInFragmentContract.Presenter {
        return logInFragmentPresenter
    }

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_login
    }

    override fun initUI() {
        progress = Progress(context, R.string.please_wait)
        setRetailerLogo()
        cbSelected.isChecked=true
        tvForgotPassword.setOnClickListener(this)
        btnLogin.setOnClickListener(this)
        cbSelected.isChecked=true
        btnNewUserRegistration.setOnClickListener(this)
        btnLoginViewOtp.setOnClickListener(this)
        btnUpdateKyc.setOnClickListener(this)
        if (CacheUtils.getRishtaUserType() == Constants.RET_USER_TYPE) {
           // btnUpdateKyc.visible = false
            cvNewUserReg.visible = false
            flLanguage.visible = false
        }

        if (CacheUtils.getSubUserType()== Constants.AC_SUB_USER_TYPE) {
            // btnUpdateKyc.visible = false
            cvNewUserReg.visible = false
           // flLanguage.visible = false
        }

        cbTermsAndConditions.setOnClickListener {
            AppUtils.showTerms(context!!)
        }


        spPreferredLanguage.setSelection(AppUtils.getSelectedLangPos())

        spPreferredLanguage.setOnTouchListener { view, motionEvent ->
            userTouch = true
            return@setOnTouchListener false
        }

        spPreferredLanguage.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0 && userTouch) {
                    userTouch = false
                    when (position) {
                        1 -> {
                            CacheUtils.setSelectedLanguage("en")
                        }
                        2 -> {
                            CacheUtils.setSelectedLanguage("hi")
                        }
                        3 -> {
                            CacheUtils.setSelectedLanguage("mr")
                        }
                        4 -> {
                            CacheUtils.setSelectedLanguage("te")
                        }
                        5 -> {
                            CacheUtils.setSelectedLanguage("bn")
                        }
                        6 -> {
                            CacheUtils.setSelectedLanguage("ml")
                        }
                        7 -> {
                            CacheUtils.setSelectedLanguage("kn")
                        }
                        8 -> {
                            CacheUtils.setSelectedLanguage("ta")
                        }

                    }
                    CacheUtils.setSendSelectedLang(true)
                    refreshCurrentActivity()
                }
            }
        }

        logInFragmentPresenter.getAppVersion()
        tvVersionName.text = "V " + BuildConfig.VERSION_NAME
    }

    private fun setRetailerLogo() {
        if (CacheUtils.getRishtaUserType() == Constants.RET_USER_TYPE) {
            ivRishtaLogo.setImageResource(R.drawable.rishta_retailer_logo)
        }
    }

    override fun showErrorDialogMsg(msg: String) {
        AppUtils.showErrorDialog(layoutInflater, context!!, msg)
    }

    override fun onResume() {
        super.onResume()
        showRedirectPlayStore()
    }

    override fun showRedirectPlayStore() {
        val dbAppVersion = CacheUtils.getDbAppVersion()
        if (dbAppVersion != null && dbAppVersion > BuildConfig.VERSION_CODE
        ) {
           // AppUtils.redirectToPlayStore(context!!, getString(R.string.update))
        }
    }

    private fun refreshCurrentActivity() {
        activity?.finish()
        activity?.startActivity(activity?.intent)
    }

    override fun showProgress() {
        progress.show()
    }

    override fun stopProgress() {
        progress.dismiss()
    }

    override fun showToast(toast: String) {
        activity?.toast(toast)
    }

    override fun showError() {
        activity?.toast(resources.getString(R.string.something_wrong))
    }


    override fun errorEnterUserName() {
        etLoginId.error = getString(R.string.enter_username)
        etLoginId.requestFocus()
    }

    override fun errorEnterOtpLoginNo() {
        etLoginViaOtp.error = getString(R.string.enter_un_for_otp_login)
        etLoginViaOtp.requestFocus()
    }

    override fun errorEnterPassword() {
        etPassword.error = getString(R.string.enter_password)
        etPassword.requestFocus()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvForgotPassword -> {
                (activity as LogInActivity).navigateToForgotPassword()
            }

            R.id.btnLogin -> {

                if (!cbSelected.isChecked) {
                    showToast(getString(R.string.please_accept_terms))
                    return
                }
                logInFragmentPresenter.doLogin(
                    etLoginId.text.toString(),
                    etPassword.text.toString(),
                    getInputs(),
                    etLoginViaOtp.text.toString()
                )
//                (activity as LogInActivity).navigateToHome()
            }

            R.id.btnLoginViewOtp -> {
                if (!cbSelected.isChecked) {
                    showToast(getString(R.string.please_accept_terms))
                    return
                }
                (activity as LogInActivity).navigateToLoginWithOtp()
            }

            R.id.btnNewUserRegistration -> {
                if (!cbSelected.isChecked) {
                    showToast(getString(R.string.please_accept_terms))
                    return
                }
                activity?.launchActivity<NewUserRegistrationActivity> { }
            }
            R.id.btnUpdateKyc -> {
                activity?.launchActivity<ReUpdateKycActivity> { }
            }
        }
    }

    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialog(layoutInflater, context!!, message)
    }

    private fun getInputs(): String {
        if (etLoginViaOtp.hasFocus()) {
            return Constants.LOGIN_TYPE_OTP
        } else {
            if (etLoginId.hasFocus() || etPassword.hasFocus()) {
                return Constants.LOGIN_TYPE_PASS
            }
        }
        return ""
    }

    override fun navigateToHome() {
        (activity as LogInActivity).navigateToHome()
    }

    override fun navigateToLoginwithOtp(loginWithOtpUserName: String) {
        (activity as LogInActivity).navigateToEnterLoginOtp(loginWithOtpUserName)
    }

}
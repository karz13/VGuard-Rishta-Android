package com.tfl.vguardrishta.ui.components.vguard.forgotPassword

import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.logIn.LogInActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import javax.inject.Inject


class ForgotPasswordFragment :
    BaseFragment<ForgotPasswordContract.View, ForgotPasswordContract.Presenter>(),
    ForgotPasswordContract.View {

    @Inject
    lateinit var forgotPasswordPresenter: ForgotPasswordPresenter

    private lateinit var progress: Progress

    override fun initUI() {
        progress = Progress(activity, R.string.please_wait)
        btnSubmit.setOnClickListener {
            forgotPasswordPresenter.validateMobileNumber(etMobileNumberRishtaId.text.toString())
        }
        setRetailerLogo()
    }

    private fun setRetailerLogo() {
        if (CacheUtils.getRishtaUserType() == Constants.RET_USER_TYPE) {
            ivRishtaLogo.setImageResource(R.drawable.rishta_retailer_logo)
        }
    }


    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialog(layoutInflater, context!!, message)
    }

    override fun navigateToLogin() {
        (activity as LogInActivity).navigateToLoginFragment()
    }

    override fun showProgress() = progress.show()

    override fun stopProgress() = progress.dismiss()

    override fun showToast(toast: String) = AppUtils.showToast(activity!!, toast)

    override fun initPresenter() = forgotPasswordPresenter

    override fun injectDependencies() =
        (activity?.application as App).applicationComponent.inject(this)

    override fun getLayoutResId() = R.layout.fragment_forgot_password

    override fun hideKeyboard() = AppUtils.hideKeyboard(activity!!)

    override fun navigateToOtpFragment() {

    }

}
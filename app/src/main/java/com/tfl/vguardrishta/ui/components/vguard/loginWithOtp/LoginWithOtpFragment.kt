package com.tfl.vguardrishta.ui.components.vguard.loginWithOtp

import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
 import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import javax.inject.Inject


/**
 * Created by Shanmuka on 3/22/2019.
 */
class LoginWithOtpFragment : BaseFragment<LoginWithOtpContract.View, LoginWithOtpContract.Presenter>(),
    LoginWithOtpContract.View {

    @Inject
    lateinit var loginWithOtpPresenter: LoginWithOtpPresenter

    private lateinit var progress: Progress

    override fun initUI() {
        progress = Progress(activity, R.string.please_wait)

        /*btnSendOtp.setOnClickListener {
            val mobileNo = etMobileNumber.text.toString().trim()
            loginWithOtpPresenter.validateMobileNumber(mobileNo)
        }*/
        setRetailerLogo()

    }

    private fun setRetailerLogo() {
        if (CacheUtils.getRishtaUserType() == Constants.RET_USER_TYPE) {
            ivRishtaLogo.setImageResource(R.drawable.rishta_retailer_logo)
        }
    }


    override fun showProgress() = progress.show()

    override fun stopProgress() = progress.dismiss()

    override fun showToast(toast: String) = AppUtils.showToast(activity!!, toast)

    override fun initPresenter() = loginWithOtpPresenter

    override fun injectDependencies() = (activity?.application as App).applicationComponent.inject(this)

    override fun getLayoutResId() = R.layout.fragment_forgot_password

    override fun hideKeyboard() = AppUtils.hideKeyboard(activity!!)

    override fun navigateToOtpFragment() {

    }

}
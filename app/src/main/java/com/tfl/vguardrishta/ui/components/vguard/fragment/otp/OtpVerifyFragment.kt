package com.tfl.vguardrishta.ui.components.vguard.fragment.otp

import android.os.CountDownTimer
import android.view.View
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.extensions.visible
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.logIn.LogInActivity
import com.tfl.vguardrishta.ui.components.vguard.fragment.newuser.NewUserRegistrationActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.fragment_otp_verify.*
import kotlinx.android.synthetic.main.fragment_otp_verify.btnSubmit
import kotlinx.android.synthetic.main.fragment_otp_verify.ivRishtaLogo
import javax.inject.Inject

class OtpVerifyFragment : BaseFragment<OtpVerifyContract.View, OtpVerifyContract.Presenter>(),
    OtpVerifyContract.View, View.OnClickListener {

    private var otpCount = 0
    private var isOtpLogin: Boolean? = false
    private var loginOtpUserName: String? = null

    @Inject
    lateinit var otpVerifyPresenter: OtpVerifyPresenter

    private lateinit var progress: Progress

    override fun initPresenter(): OtpVerifyContract.Presenter {
        return otpVerifyPresenter
    }

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_otp_verify
    }

    private fun setRetailerLogo() {
        if (CacheUtils.getRishtaUserType() == Constants.RET_USER_TYPE) {
            ivRishtaLogo.setImageResource(R.drawable.rishta_retailer_logo)
        }
    }

    override fun initUI() {
        progress = Progress(context, R.string.please_wait)
        setRetailerLogo()
        btnSubmit.setOnClickListener(this)
        startCountDownTimer()
        isOtpLogin = activity?.intent?.getBooleanExtra(Constants.IS_LOGIN_WITH_OTP, false)
        loginOtpUserName = activity?.intent?.getStringExtra(Constants.LOGIN_OTP_USER_NAME)
        tvResend.setOnClickListener {
            if (otpCount == 5) {
                showToast(getString(R.string.maximum_otp_count))
                return@setOnClickListener
            }
            otpCount += 1
            tvResend.isEnabled = false
            tvResend.isClickable = false
            tvResend.alpha = 0.4f

            tvCallToGetOtp.isEnabled = false
            tvCallToGetOtp.isClickable = false
            tvCallToGetOtp.alpha = 0.4f

            tvSeconds.visible = true
            if (isOtpLogin!!) {
                otpVerifyPresenter.generateOtpForLogin(loginOtpUserName!!)
            } else {
                otpVerifyPresenter.generateOtpForNewUser(null)
            }
            startCountDownTimer()
        }

        tvCallToGetOtp.setOnClickListener {
            if (otpCount == 5) {
                showToast(getString(R.string.maximum_otp_count))
                return@setOnClickListener
            }
            otpCount += 1
            tvCallToGetOtp.isEnabled = false
            tvCallToGetOtp.isClickable = false
            tvCallToGetOtp.alpha = 0.4f
            tvResend.isEnabled = false
            tvResend.isClickable = false
            tvResend.alpha = 0.4f
            tvSeconds.visible = true
            if (isOtpLogin!!) {
                otpVerifyPresenter.generateOtpForLogin(loginOtpUserName!!)
            } else {
                otpVerifyPresenter.generateOtpForNewUser(Constants.OTP_TYPE_VOICE)
            }
            startCountDownTimer()
        }
    }

    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialog(layoutInflater, context!!, message)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSubmit -> {
                if (isOtpLogin!!) {
                    val otp = etOTP.text.toString()
                    otpVerifyPresenter.verifyLoginOtp(otp, loginOtpUserName)
                } else {
                    val otp = etOTP.text.toString()
                    otpVerifyPresenter.verifyOtp(otp)
                }
            }
        }
    }


    override fun navigateToRishtaHome() {
        (activity as LogInActivity).navigateToHome()
    }

    private fun startCountDownTimer() {
        object : CountDownTimer(60000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                if (tvSeconds == null) return
                tvSeconds.text =
                    getString(R.string.in_) + " " + millisUntilFinished / 1000 + " " + getString(R.string.seconds)
            }

            override fun onFinish() {
                if (tvSeconds == null) return
                if (tvResend == null) return
                tvResend.isEnabled = true
                tvResend.isClickable = true
                tvResend.alpha = 1f
                tvSeconds.visible = false

                tvCallToGetOtp.isEnabled = true
                tvCallToGetOtp.isClickable = true
                tvCallToGetOtp.alpha = 1f
            }
        }.start()

    }

    override fun navigateToNewUserReg() {
        if (activity is NewUserRegistrationActivity) {
            (activity as NewUserRegistrationActivity).navigateToUserPersonalDetails()
        }
    }

}
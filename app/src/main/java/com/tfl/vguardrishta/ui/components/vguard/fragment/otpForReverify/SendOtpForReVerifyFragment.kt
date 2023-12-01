package com.tfl.vguardrishta.ui.components.vguard.fragment.otpForReverify

import android.os.CountDownTimer
import android.view.View
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.BuildConfig
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.extensions.visible
import com.tfl.vguardrishta.models.FileUploader
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.logIn.LogInActivity
import com.tfl.vguardrishta.ui.components.reupdateKyc.ReUpdateKycActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.fragment_login_with_otp.*
import javax.inject.Inject

class SendOtpForReVerifyFragment :
    BaseFragment<SendOtpForReVerifyContract.View, SendOtpForReVerifyContract.Presenter>(),
    SendOtpForReVerifyContract.View, View.OnClickListener {
    private lateinit var progress: Progress
    private var otpCount = 0

    @Inject
    lateinit var sendOtpForReVerifyPresenter: SendOtpForReVerifyPresenter


    override fun initPresenter(): SendOtpForReVerifyContract.Presenter {
        return sendOtpForReVerifyPresenter
    }

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_login_with_otp
    }

    override fun initUI() {
        progress = Progress(context, R.string.please_wait)
        btnLoginViewOtp.setOnClickListener(this)
        btnLoginViewOtp.setText(getString(R.string.send_otp))
        llOtpThroughCall.setOnClickListener(this)
        CacheUtils.setFileUploader(FileUploader())
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
            sendOtpForReVerifyPresenter.createOtp(etLoginId.text.toString(), null)

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
            sendOtpForReVerifyPresenter.createOtp(
                etLoginId.text.toString(),
                Constants.OTP_TYPE_VOICE
            )

            startCountDownTimer()
        }


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
            }
        }.start()

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

    }

    override fun errorEnterOtpLoginNo() {
        etLoginId.error = getString(R.string.enter_registered_mobile_no)
        etLoginId.requestFocus()
    }

    override fun errorEnterPassword() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnLoginViewOtp -> {
                if (btnLoginViewOtp.text == getString(R.string.send_otp)) {
                    sendOtpForReVerifyPresenter.createOtp(etLoginId.text.toString(), null)
                    startCountDownTimer()

                } else {
                    val otp = etOtp.text.toString()
                    sendOtpForReVerifyPresenter.verifyLoginOtp(otp, etLoginId.text.toString())
                }
            }

            R.id.llOtpThroughCall -> {
                sendOtpForReVerifyPresenter.createOtp(
                    etLoginId.text.toString(),
                    Constants.OTP_TYPE_VOICE
                )
                startCountDownTimer()

            }
        }
    }

    override fun navigateToReUpdateKycFragment() {
        (activity as ReUpdateKycActivity).navigateToReUpdateKycFragment()
    }

    override fun navigateToRetailerReUpdateKycFragment() {
        (activity as ReUpdateKycActivity).navigateToRetReUpdateKycFragment()
    }

    override fun showRedirectPlayStore() {
        val dbAppVersion = CacheUtils.getDbAppVersion()
        if (dbAppVersion != null && dbAppVersion > BuildConfig.VERSION_CODE
        ) {
            AppUtils.redirectToPlayStore(context!!, getString(R.string.update))
        }
    }

    override fun showEnterOtp() {
        cvOtp.visibility = View.VISIBLE
        llOtpThroughCall.visibility = View.GONE
        btnLoginViewOtp.setText(getString(R.string.submit))
    }

    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialog(layoutInflater, context!!, message)
    }


    override fun navigateToLoginwithOtp(loginWithOtpUserName: String) {
        (activity as LogInActivity).navigateToEnterLoginOtp(loginWithOtpUserName)
    }

}
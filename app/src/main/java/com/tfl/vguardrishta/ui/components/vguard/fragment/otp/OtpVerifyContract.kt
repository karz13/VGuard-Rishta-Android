package com.tfl.vguardrishta.ui.components.vguard.fragment.otp

import com.tfl.vguardrishta.ui.base.BaseContract

interface OtpVerifyContract {

    interface View : BaseContract.View {

        fun initUI()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun navigateToNewUserReg()
        fun navigateToRishtaHome()
        fun showMsgDialog(message: String)


    }

    interface Presenter : BaseContract.Presenter<View> {
        fun verifyOtp(otp: String)
        fun generateOtpForNewUser(otpTypeVoice: String?)
        fun generateOtpForLogin(loginOtpUserName: String)
        fun verifyLoginOtp(otp: String, loginOtpUserName: String?)


    }

}
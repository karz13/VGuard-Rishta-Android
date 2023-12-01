package com.tfl.vguardrishta.ui.components.vguard.fragment.otpForReverify

import android.app.Activity
import com.tfl.vguardrishta.ui.base.BaseContract

interface SendOtpForReVerifyContract {

    interface View : BaseContract.View {

        fun initUI()

        fun showProgress() {}

        fun stopProgress() {}

        fun showToast(toast: String) {}

        fun errorEnterUserName() {}

        fun errorEnterPassword() {}

        fun  showError(){}

        fun finishView() {}

        fun redirectToPlayStore() {}

        fun getActivity(): Activity?
        fun errorEnterOtpLoginNo(){}
        fun navigateToLoginwithOtp(loginWithOtpUserName: String) {}
        fun showMsgDialog(message: String){}
        fun showEnterOtp()
        fun navigateToReUpdateKycFragment()
        fun navigateToRetailerReUpdateKycFragment()
        fun showRedirectPlayStore()

    }

    interface Presenter : BaseContract.Presenter<View> {

        fun getAppVersion()

        fun createOtp(toString: String, otpType: String?)
         fun verifyLoginOtp(otp: String, loginOtpUserName: String?)
    }

}
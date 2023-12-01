package com.tfl.vguardrishta.ui.components.vguard.forgotPassword

import com.tfl.vguardrishta.ui.base.BaseContract

/**
 * Created by Shanmuka on 3/21/2019.
 */
interface ForgotPasswordContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView(){}

        fun showProgress(){}

        fun stopProgress(){}

        fun showToast(toast: String){}

        fun navigateToOtpFragment(){}

        fun hideKeyboard(){}
        fun navigateToLogin(){}

        fun showMsgDialog(it1: String) {}

    }

    interface Presenter : BaseContract.Presenter<View> {

        fun validateMobileNumber(mobileNo: String)

    }
}
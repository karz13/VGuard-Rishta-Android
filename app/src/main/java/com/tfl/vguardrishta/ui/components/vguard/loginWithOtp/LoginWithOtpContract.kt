package com.tfl.vguardrishta.ui.components.vguard.loginWithOtp

import com.tfl.vguardrishta.ui.base.BaseContract

/**
 * Created by Shanmuka on 3/21/2019.
 */
interface LoginWithOtpContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView() {}

        fun showProgress() {}

        fun stopProgress() {}

        fun showToast(toast: String) {}

        fun navigateToOtpFragment() {}

        fun hideKeyboard()

    }

    interface Presenter : BaseContract.Presenter<View> {

        fun validateMobileNumber(mobileNo: String)

    }
}
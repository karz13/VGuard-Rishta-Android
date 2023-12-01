package com.tfl.vguardrishta.ui.components.vguard.redeempoints

import com.tfl.vguardrishta.ui.base.BaseContract

interface RedeemPointContract {

    interface View : BaseContract.View {

        fun initUI()


        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showUserDetails()

    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getUserDetails()


    }

}
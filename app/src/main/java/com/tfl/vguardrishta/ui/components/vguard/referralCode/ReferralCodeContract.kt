package com.tfl.vguardrishta.ui.components.vguard.referralCode

import com.tfl.vguardrishta.ui.base.BaseContract

interface ReferralCodeContract {

    interface View : BaseContract.View {

        fun initUI()
        fun showProgress()
        fun stopProgress()
        fun showToast(toast: String)
        fun showError()
        fun showNoData()
     }

    interface Presenter : BaseContract.Presenter<View> {
    }

}
package com.tfl.vguardrishta.ui.components.vguard.fragment.contactUs

import com.tfl.vguardrishta.models.Notifications
import com.tfl.vguardrishta.models.RedemptionOrder
import com.tfl.vguardrishta.models.User
import com.tfl.vguardrishta.ui.base.BaseContract

interface ContactUsContract {
    interface View : BaseContract.View {

        fun initUI()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()

     }

    interface Presenter : BaseContract.Presenter<View> {

    }

}
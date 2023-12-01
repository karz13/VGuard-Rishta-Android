package com.tfl.vguardrishta.ui.components.vguard.fragment.home

import com.tfl.vguardrishta.models.RedemptionOrder
import com.tfl.vguardrishta.models.User
import com.tfl.vguardrishta.ui.base.BaseContract

interface RishtaHomeContract {

    interface View : BaseContract.View {

        fun initUI()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()

         fun showUserDetails()
        fun showNotificationCount(count: Int)
        fun autoLogoutUser()


    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getUserDetails()
        fun getNotificationCount()
    }

}
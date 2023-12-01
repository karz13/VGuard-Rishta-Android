package com.tfl.vguardrishta.ui.components.vguard.home

import com.tfl.vguardrishta.models.Notifications
import com.tfl.vguardrishta.models.VguardRishtaUser
import com.tfl.vguardrishta.ui.base.BaseContract

interface HomeContract {

    interface View : BaseContract.View {

        fun initUI()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showLogoutDialog()
        fun logoutUser()
        fun showNotificationCount(count: Int)
        fun showNotificationDialog(it: List<Notifications>)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getUserDetails()
        fun logoutUser()
        fun sendSelectedLang(selectedLangId: Int)
        fun getNotificationCount()
        fun updateFcmToken(vguardRishtaUser: VguardRishtaUser)
    }

}
package com.tfl.vguardrishta.ui.components.vguard.fragment.alert

import com.tfl.vguardrishta.models.Notifications
import com.tfl.vguardrishta.models.RedemptionOrder
import com.tfl.vguardrishta.models.User
import com.tfl.vguardrishta.ui.base.BaseContract

interface NotificationContract {
    interface View : BaseContract.View {

        fun initUI()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showNotifications(it: List<Notifications>)
        fun showNoData()
        fun showNotificationCount(count: Int)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getNotifications()
        fun getNotificationCount()
        fun sendNotificationRead(it: Notifications)

    }

}
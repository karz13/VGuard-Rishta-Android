package com.tfl.vguardrishta.ui.components.vguard.dashboard

import com.tfl.vguardrishta.models.PointsSummary
import com.tfl.vguardrishta.ui.base.BaseContract

interface DashboardContract {

    interface View : BaseContract.View {

        fun initUI()


        fun showProgress() {}

        fun stopProgress() {}

        fun showToast(toast: String) {}

        fun showError()
        fun showPointsSummary(it: PointsSummary)
        fun showUserDetails()


    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getMonthWiseEarning(month: Int, year: Int)
        fun getUserDetails()


    }

}
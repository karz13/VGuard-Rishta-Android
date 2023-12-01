package com.tfl.vguardrishta.ui.components.vguard.airCooler

import com.tfl.vguardrishta.models.PointsSummary
import com.tfl.vguardrishta.ui.base.BaseContract

interface AirCoolerContract {

    interface View : BaseContract.View {

        fun initUI()


        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showUserDetails(pointsSummary: PointsSummary)
        fun showSchemeDetails(it: String)

    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getAirCoolerPointSummary()
        fun getAirCoolerSchemeDetails()

    }

}
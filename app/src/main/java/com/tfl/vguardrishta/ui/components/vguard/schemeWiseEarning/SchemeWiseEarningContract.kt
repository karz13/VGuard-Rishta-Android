package com.tfl.vguardrishta.ui.components.vguard.schemeWiseEarning

import com.tfl.vguardrishta.models.SchemeWiseEarning
import com.tfl.vguardrishta.ui.base.BaseContract

interface SchemeWiseEarningContract {

    interface View : BaseContract.View {

        fun initUI()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showNoData()
        fun showEarningDetails(it: List<SchemeWiseEarning>)

    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getEarningDetails()


    }

}
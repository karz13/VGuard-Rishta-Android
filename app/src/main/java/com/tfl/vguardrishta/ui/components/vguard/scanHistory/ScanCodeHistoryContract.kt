package com.tfl.vguardrishta.ui.components.vguard.scanHistory

import com.tfl.vguardrishta.models.CouponResponse
import com.tfl.vguardrishta.ui.base.BaseContract

interface ScanCodeHistoryContract {

    interface View : BaseContract.View {

        fun initUI()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showNoData()
        fun showScanHistory(it: List<CouponResponse>)

    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getTicketHistory()
        fun getAirCoolerScanCodeHistory()


    }

}
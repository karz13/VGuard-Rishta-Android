package com.tfl.vguardrishta.ui.components.vguard.ticketHistory

import com.tfl.vguardrishta.models.TicketType
import com.tfl.vguardrishta.ui.base.BaseContract

interface TicketHistoryContract {

    interface View : BaseContract.View {

        fun initUI()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showNoData()
        fun showTicketHistory(it: List<TicketType>?)

    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getTicketHistory()


    }

}
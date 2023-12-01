package com.tfl.vguardrishta.ui.components.vguard.raiseTicket

import android.content.Context
import com.tfl.vguardrishta.models.TicketType
import com.tfl.vguardrishta.ui.base.BaseContract

interface RaiseTicketContract {

    interface View : BaseContract.View {

        fun initUI()

        fun getContext():Context

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showTicketTypes(it: List<TicketType>)

        fun hideKeyBoard()
        fun showMsgDialog(message: String)
        fun showMsgDialogWithFinish(message: String)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getTicketTypes()
        fun submit(ticketType: TicketType, issueDesc: String)


    }

}
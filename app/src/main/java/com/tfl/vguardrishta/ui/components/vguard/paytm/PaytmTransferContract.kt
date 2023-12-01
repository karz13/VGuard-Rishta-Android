package com.tfl.vguardrishta.ui.components.vguard.paytm

import com.tfl.vguardrishta.models.ProductDetail
import com.tfl.vguardrishta.ui.base.BaseContract

interface PaytmTransferContract {

    interface View : BaseContract.View {

        fun initUI()


        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()

        fun hideKeyBoard()
        fun setPaytmProductId(it: ProductDetail?)
        fun showMsgDialog(message: String)
        fun showMsgDialogWithFinish(message: String)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun validateAndTransfer(points: String, mobile: String,screenType : String?)
        fun getPaytmProductId()

    }

}
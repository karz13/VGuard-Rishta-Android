package com.tfl.vguardrishta.ui.components.vguard.productRegisteration

import com.tfl.vguardrishta.models.ProductDetail
import com.tfl.vguardrishta.ui.base.BaseContract

interface ProductRegistrationContract {

    interface View : BaseContract.View {

        fun initUI()


        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()

        fun hideKeyBoard()

        fun showMsgDialog(message: String)
        fun showMsgDialogWithFinish(message: String)
        fun navigateToScanProduct()
    }

    interface Presenter : BaseContract.Presenter<View> {


    }

}
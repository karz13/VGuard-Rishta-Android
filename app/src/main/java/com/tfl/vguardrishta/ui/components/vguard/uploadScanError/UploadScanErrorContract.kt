package com.tfl.vguardrishta.ui.components.vguard.uploadScanError

import com.tfl.vguardrishta.ui.base.BaseContract

interface UploadScanErrorContract {

    interface View : BaseContract.View {

        fun initUI()

        fun hideKeyBoard()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showMsgDialogWithFinish(message: String)

    }

    interface Presenter : BaseContract.Presenter<View> {
        fun uploadScanError(remarks: String, barCode: String)

    }

}
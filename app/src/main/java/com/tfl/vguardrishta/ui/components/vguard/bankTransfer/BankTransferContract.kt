package com.tfl.vguardrishta.ui.components.vguard.bankTransfer

import com.tfl.vguardrishta.ui.base.BaseContract
import com.tfl.vguardrishta.models.BankDetail
import com.tfl.vguardrishta.models.VguardRishtaUser

interface BankTransferContract {

    interface View : BaseContract.View {

        fun initUI()

        fun hideKeyBoard()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showMsgDialog(message: String)
        fun showBankDetail(it: BankDetail)
        fun showBanks(it: List<BankDetail>)
        fun showMsgDialogWithFinish(message: String)
        fun setRishtaUser(it: VguardRishtaUser?)

    }

    interface Presenter : BaseContract.Presenter<View> {
        fun validateDetail(
            amount: String,
            accNo: String,
            accHolderName: String,
            accType: String,
            bankAndBranch: String,
            ifscCode: String,
            selectedBankDetail: String
        )

        fun getUserBankDetail()
        fun getBanks()
        fun getProfile()


    }

}
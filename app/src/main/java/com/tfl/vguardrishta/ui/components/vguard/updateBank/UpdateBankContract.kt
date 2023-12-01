package com.tfl.vguardrishta.ui.components.vguard.updateBank

import com.tfl.vguardrishta.models.BankDetail
import com.tfl.vguardrishta.models.KycDetails
import com.tfl.vguardrishta.models.KycIdTypes
import com.tfl.vguardrishta.ui.base.BaseContract
import java.util.*

class UpdateBankContract {

    interface View : BaseContract.View {

        fun initUI()

        fun hideKeyBoard()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun setKycTypesSpinner(arrayList: ArrayList<KycIdTypes>)
        fun showMsgDialog(message: String)
        fun finishView()
        fun showKycDetails(it: KycDetails)
        fun showBanks(arrayList: ArrayList<BankDetail>)

    }

    interface Presenter : BaseContract.Presenter<View> {
        fun updateBankDetails(
            bankDetail: BankDetail
        )

        fun getKycIdTypes()
        fun getKycDetails()
        fun getBanks()
        fun validate(bankDetail: BankDetail)
    }
}
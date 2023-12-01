package com.tfl.vguardrishta.ui.components.vguard.updateKycRetailer

import com.tfl.vguardrishta.models.KycRetailerDetails
import com.tfl.vguardrishta.models.KycIdTypes
import com.tfl.vguardrishta.ui.base.BaseContract
import com.tfl.vguardrishta.models.KycDetails
import java.util.*

class UpdateKycRetailerContract {

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

    }

    interface Presenter : BaseContract.Presenter<View> {
        fun uploadKycDetails(
            kycDetails: KycRetailerDetails
        )
        fun getKycIdTypes()
        fun getKycDetails()


    }

}
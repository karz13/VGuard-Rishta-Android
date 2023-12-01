package com.tfl.vguardrishta.ui.components.vguard.updateKyc

import com.tfl.vguardrishta.models.KycDetails
import com.tfl.vguardrishta.models.KycIdTypes
import com.tfl.vguardrishta.ui.base.BaseContract
import java.util.ArrayList

interface UpdateKycContract {

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
            kycDetails: KycDetails
        )
        fun getKycIdTypes()
        fun getKycDetails()


    }

}
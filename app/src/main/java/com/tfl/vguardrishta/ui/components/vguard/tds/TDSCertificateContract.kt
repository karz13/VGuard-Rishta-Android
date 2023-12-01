package com.tfl.vguardrishta.ui.components.vguard.tds

import com.tfl.vguardrishta.models.DownloadData
import com.tfl.vguardrishta.models.TdsData
import com.tfl.vguardrishta.ui.base.BaseContract

class TDSCertificateContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView() {}

        fun showProgress()

        fun stopProgress()

        fun showToast(msg: String)

        fun showError(){}

        fun showNoData()
        fun showWelfare(it: List<TdsData>)

        fun setAccementSpinner(arrayList: ArrayList<String>)


    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getTDSCertificate(accessmentYear : String)
        fun getAccessmentYear()

    }

}
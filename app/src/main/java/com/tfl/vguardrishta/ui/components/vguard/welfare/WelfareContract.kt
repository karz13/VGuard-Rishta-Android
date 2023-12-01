package com.tfl.vguardrishta.ui.components.vguard.welfare

import com.tfl.vguardrishta.models.DownloadData
import com.tfl.vguardrishta.ui.base.BaseContract
import com.tfl.vguardrishta.models.WhatsNew

interface WelfareContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView() {}

        fun showProgress()

        fun stopProgress()

        fun showToast(msg: String)

        fun showError(){}

        fun showNoData()
        fun showWelfare(it: List<DownloadData>)


    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getWelfare()


    }

}
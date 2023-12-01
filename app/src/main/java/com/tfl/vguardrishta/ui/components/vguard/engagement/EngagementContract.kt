package com.tfl.vguardrishta.ui.components.vguard.engagement

import com.tfl.vguardrishta.models.DownloadData
import com.tfl.vguardrishta.models.TdsData
import com.tfl.vguardrishta.ui.base.BaseContract

class EngagementContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView() {}

        fun showProgress()

        fun stopProgress()

        fun showToast(msg: String)

        fun showError(){}


    }

    interface Presenter : BaseContract.Presenter<View> {

    }

}
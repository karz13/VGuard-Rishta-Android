package com.tfl.vguardrishta.ui.components.vguard.tierDetail

import com.tfl.vguardrishta.models.TierPoints
import com.tfl.vguardrishta.ui.base.BaseContract
import com.tfl.vguardrishta.models.WhatsNew

interface TierDetailContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView() {}

        fun showProgress() {}

        fun stopProgress() {}

        fun showToast(toast: String) {}

        fun showError(){}
        fun showTierDetail(it: TierPoints?)


    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getTierDetails()


    }

}
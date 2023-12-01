package com.tfl.vguardrishta.ui.components.vguard.redemptionHistory

import com.tfl.vguardrishta.models.RedemptionHistory
import com.tfl.vguardrishta.ui.base.BaseContract

interface RedemptionHistoryContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView() {}

        fun showProgress() {}

        fun stopProgress() {}

        fun showToast(toast: String) {}

        fun showError() {}
        fun showNoData()
        fun showRedeemptionHistory(it: List<RedemptionHistory>)
        fun showTrackRedemption(orderId: String?)


    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getHistory(type:String)
        fun trackRedemtion(it: RedemptionHistory)

    }

}
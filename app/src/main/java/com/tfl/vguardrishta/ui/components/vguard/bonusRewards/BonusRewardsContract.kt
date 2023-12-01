package com.tfl.vguardrishta.ui.components.vguard.bonusRewards

import com.tfl.vguardrishta.models.CouponResponse
import com.tfl.vguardrishta.ui.base.BaseContract

interface BonusRewardsContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView() {}

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showNoData()
        fun showBonusRewards(it: List<CouponResponse>)


    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getBonusRewards()


    }

}
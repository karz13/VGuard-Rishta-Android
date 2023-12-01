package com.tfl.vguardrishta.ui.components.vguard.returnScan

import com.tfl.vguardrishta.models.CouponResponse
import com.tfl.vguardrishta.ui.base.BaseContract

/**
 * Created by Shanmuka on 1/16/2019.
 */
interface ReturnScanContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView()

        fun showProgress()

        fun stopProgress()

        fun showToast(message: String)

        fun showLongToast(message: String)

        fun clearCoupon()
    }

    interface Presenter : BaseContract.Presenter<View> {

    }
}
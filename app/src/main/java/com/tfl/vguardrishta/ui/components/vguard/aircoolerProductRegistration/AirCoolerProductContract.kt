package com.tfl.vguardrishta.ui.components.vguard.aircoolerProductRegistration

import com.tfl.vguardrishta.models.CouponResponse
import com.tfl.vguardrishta.models.CustomerDetailsRegistration
import com.tfl.vguardrishta.ui.base.BaseContract

interface AirCoolerProductContract {

    interface View : BaseContract.View {

        fun initUI()
        fun showProgress()
        fun stopProgress()
        fun showToast(toast: String)
        fun showError()
        fun showCouponPoints(it: CouponResponse?)
        fun showErrorDialog(errorMsg: String?)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun sendDataToServer(cdr: CustomerDetailsRegistration)
    }

}
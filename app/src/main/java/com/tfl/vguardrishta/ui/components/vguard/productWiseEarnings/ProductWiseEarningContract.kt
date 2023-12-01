package com.tfl.vguardrishta.ui.components.vguard.productWiseEarnings

import com.tfl.vguardrishta.models.ProductWiseEarning
import com.tfl.vguardrishta.ui.base.BaseContract

interface ProductWiseEarningContract {

    interface View : BaseContract.View {

        fun initUI()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showNoData()
        fun showEarningDetails(it: List<ProductWiseEarning>)

    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getEarningDetails()


    }

}
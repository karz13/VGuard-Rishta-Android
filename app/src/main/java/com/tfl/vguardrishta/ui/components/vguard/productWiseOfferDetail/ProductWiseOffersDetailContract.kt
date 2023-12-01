package com.tfl.vguardrishta.ui.components.vguard.productWiseOfferDetail

import com.tfl.vguardrishta.models.ProductWiseOffersDetail
import com.tfl.vguardrishta.models.TicketType
import com.tfl.vguardrishta.ui.base.BaseContract

interface ProductWiseOffersDetailContract {

    interface View : BaseContract.View {

        fun initUI()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showNoData()
         fun showOfferDetails(it: List<ProductWiseOffersDetail>?)

    }

    interface Presenter : BaseContract.Presenter<View> {
         fun getDetails(catId: String?)


    }

}
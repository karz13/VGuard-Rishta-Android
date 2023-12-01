package com.tfl.vguardrishta.ui.components.vguard.productWiseOffers

import com.tfl.vguardrishta.models.ProductWiseOffers
import com.tfl.vguardrishta.ui.base.BaseContract

interface ProductWiseOfferContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView() {}

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showNoData()
        fun showProductWiseOffers(it: List<ProductWiseOffers>)
        fun navigateToOfferDetails(categoryId: String?)


    }

    interface Presenter : BaseContract.Presenter<View> {


        fun getProductWiseOffers()
        fun getProductWiseOffersDetail(it: ProductWiseOffers)
    }

}
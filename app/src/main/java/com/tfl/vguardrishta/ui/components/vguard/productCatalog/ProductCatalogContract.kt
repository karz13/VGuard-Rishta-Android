package com.tfl.vguardrishta.ui.components.vguard.productCatalog

import com.tfl.vguardrishta.models.ProductDetail
import com.tfl.vguardrishta.models.SchemeWiseEarning
import com.tfl.vguardrishta.ui.base.BaseContract

interface ProductCatalogContract {

    interface View : BaseContract.View {

        fun initUI()
        fun showProgress()
        fun stopProgress()
        fun showToast(toast: String)
        fun showError()
        fun showNoData()
        fun setProductsCatalog(list: List<ProductDetail>)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getProductCatalog()
    }

}
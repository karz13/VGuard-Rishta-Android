package com.tfl.vguardrishta.ui.components.vguard.productWiseOffers

import android.content.Context
import android.util.Log
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.ProductWiseOfferUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.ProductWiseOffers
import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject

class ProductWiseOfferPresenter @Inject constructor(
    val prodWiseOffer: ProductWiseOfferUseCase,
    val context: Context
) : BasePresenter<ProductWiseOfferContract.View>(), ProductWiseOfferContract.Presenter {


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getProductWiseOffers() {
        disposables?.add(prodWiseOffer.getProductWiseOffers().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (!it.isNullOrEmpty()) {
                    getView()?.showProductWiseOffers(it)
                } else {
                    getView()?.showNoData()
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun getProductWiseOffersDetail(it: ProductWiseOffers) {
        getView()?.navigateToOfferDetails(it.categoryId)
    }
}
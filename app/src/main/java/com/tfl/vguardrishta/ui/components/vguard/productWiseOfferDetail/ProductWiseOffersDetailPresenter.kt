package com.tfl.vguardrishta.ui.components.vguard.productWiseOfferDetail

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.ProductWiseOfferUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject

class ProductWiseOffersDetailPresenter @Inject constructor(
    private val context: Context,
    private val offerUseCase: ProductWiseOfferUseCase
) : BasePresenter<ProductWiseOffersDetailContract.View>(),
    ProductWiseOffersDetailContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getDetails(catId: String?) {
        disposables?.add(
            offerUseCase.execute(catId!!).applySchedulers()
                .doOnSubscribe { getView()?.showProgress() }.doFinally { getView()?.stopProgress() }
                .subscribe({
                    if (it != null && !it.isNullOrEmpty()) {
                        getView()?.showOfferDetails(it)
                    } else {
                        getView()?.showNoData()
                    }
                },
                    {
                        getView()?.showToast(context.getString(R.string.something_wrong))
                    })
        )
    }
}
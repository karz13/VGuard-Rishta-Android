package com.tfl.vguardrishta.ui.components.vguard.productRegisteration

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetPaytmProductIdUseCase
import com.tfl.vguardrishta.domain.PaytmTransferUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.ProductOrder
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import javax.inject.Inject

class ProductRegistrationPresenter @Inject constructor(
    val context: Context,
    val paytmTransferUseCase: PaytmTransferUseCase,
    val getPaytmProductIdUseCase: GetPaytmProductIdUseCase
) : BasePresenter<ProductRegistrationContract.View>(), ProductRegistrationContract.Presenter {


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }


}
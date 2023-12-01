package com.tfl.vguardrishta.ui.components.vguard.returnScan

import android.content.Context
import com.tfl.vguardrishta.domain.CaptureSaleCase
import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject


class ReturnScanPresenter @Inject constructor(
    private val context: Context,
    private val captureCaseSale: CaptureSaleCase
) : BasePresenter<ReturnScanContract.View>(),
    ReturnScanContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }
}
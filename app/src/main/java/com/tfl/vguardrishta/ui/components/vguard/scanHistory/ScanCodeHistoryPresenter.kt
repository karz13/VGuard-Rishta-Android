package com.tfl.vguardrishta.ui.components.vguard.scanHistory

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetScanCodeHistoryUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject

class ScanCodeHistoryPresenter @Inject constructor(
    private val context: Context,
    private val getScanCodeHistoryUseCase: GetScanCodeHistoryUseCase
) : BasePresenter<ScanCodeHistoryContract.View>(), ScanCodeHistoryContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getTicketHistory() {
        disposables?.add(
            getScanCodeHistoryUseCase.execute().applySchedulers()
                .doOnSubscribe { getView()?.showProgress() }.doFinally { getView()?.stopProgress() }
                .subscribe({
                    if (!it.isNullOrEmpty()) {
                        getView()?.showScanHistory(it)
                    } else {
                        getView()?.showNoData()
                    }
                },
                    {
                        getView()?.showToast(context.getString(R.string.something_wrong))
                    })
        )
    }

    override fun getAirCoolerScanCodeHistory() {
        disposables?.add(
            getScanCodeHistoryUseCase.getAirCoolerScanCodeHistory().applySchedulers()
                .doOnSubscribe { getView()?.showProgress() }.doFinally { getView()?.stopProgress() }
                .subscribe({
                    if (!it.isNullOrEmpty()) {
                        getView()?.showScanHistory(it)
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
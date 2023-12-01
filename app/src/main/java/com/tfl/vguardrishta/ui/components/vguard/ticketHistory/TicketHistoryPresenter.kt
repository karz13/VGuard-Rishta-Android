package com.tfl.vguardrishta.ui.components.vguard.ticketHistory

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetTicketHistoryUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.CacheUtils
import javax.inject.Inject

class TicketHistoryPresenter @Inject constructor(
    private val context: Context,
    private val getTicketHistoryUseCase: GetTicketHistoryUseCase
) : BasePresenter<TicketHistoryContract.View>(), TicketHistoryContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getTicketHistory() {
        disposables?.add(
            getTicketHistoryUseCase.execute(Unit).applySchedulers()
                .doOnSubscribe { getView()?.showProgress() }.doFinally { getView()?.stopProgress() }
                .subscribe({
                    if (it != null && !it.isNullOrEmpty()) {
                        getView()?.showTicketHistory(it)
                    }else{
                        getView()?.showNoData()
                    }
                },
                    {
                        getView()?.showToast(context.getString(R.string.something_wrong))
                    })
        )
    }
}
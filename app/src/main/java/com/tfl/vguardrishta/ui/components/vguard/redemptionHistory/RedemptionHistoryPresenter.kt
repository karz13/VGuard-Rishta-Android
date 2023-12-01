package com.tfl.vguardrishta.ui.components.vguard.redemptionHistory

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetRedeemHistoryUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.RedemptionHistory
import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject

class RedemptionHistoryPresenter @Inject constructor(
    val context: Context,
    val getRedeemHistoryUseCase: GetRedeemHistoryUseCase
) : BasePresenter<RedemptionHistoryContract.View>(), RedemptionHistoryContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getHistory(type: String) {
        disposables?.add(getRedeemHistoryUseCase.execute(type).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (!it.isNullOrEmpty()) {
                    getView()?.showRedeemptionHistory(it)
                } else {
                    getView()?.showNoData()
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }


    override fun trackRedemtion(it: RedemptionHistory) {
        getView()?.showTrackRedemption(it.orderId)
    }

}
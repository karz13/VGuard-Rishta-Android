package com.tfl.vguardrishta.ui.components.vguard.dailyWinner

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetWhatsNewUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.DailyWinner
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.CacheUtils
import javax.inject.Inject

class DailyWinnerPresenter @Inject constructor(
    val whatsNewUseCase: GetWhatsNewUseCase,
    val context: Context
) : BasePresenter<DailyWinnerContract.View>(), DailyWinnerContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getDailyWinner(date: DailyWinner) {
        disposables?.add(whatsNewUseCase.getDailyWinner(date).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (!it.isNullOrEmpty()) {
                    getView()?.setDailyWinners(it)
                } else {
                    getView()?.showNoData()
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun getDailyWinnerDates() {
        disposables?.add(whatsNewUseCase.getDailyWinnerDates().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                val firstWinnerDate = CacheUtils.getFirstWinnerDate(context)
                firstWinnerDate.addAll(it)
                getView()?.setDailyWInnerSp(firstWinnerDate)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }
}
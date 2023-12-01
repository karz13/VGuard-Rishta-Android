package com.tfl.vguardrishta.ui.components.vguard.dailyWinner

import com.tfl.vguardrishta.models.DailyWinner
import com.tfl.vguardrishta.ui.base.BaseContract

interface DailyWinnerContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView() {}

        fun showProgress() {}

        fun stopProgress() {}

        fun showToast(toast: String) {}

        fun showError(){}
        fun setDailyWinners(it: List<DailyWinner>)
        fun showNoData()
        fun setDailyWInnerSp(it: List<DailyWinner>)


    }

    interface Presenter : BaseContract.Presenter<View> {
         fun getDailyWinner(date: DailyWinner)
        fun getDailyWinnerDates()


    }

}
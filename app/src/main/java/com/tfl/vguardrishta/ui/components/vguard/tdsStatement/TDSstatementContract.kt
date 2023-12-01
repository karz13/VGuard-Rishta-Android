package com.tfl.vguardrishta.ui.components.vguard.tdsStatement

import com.tfl.vguardrishta.models.DownloadData
import com.tfl.vguardrishta.models.MonthData
import com.tfl.vguardrishta.models.TdsData
import com.tfl.vguardrishta.models.TdsStatement
import com.tfl.vguardrishta.ui.base.BaseContract

class TDSstatementContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView() {}

        fun showProgress()

        fun stopProgress()

        fun showToast(msg: String)

        fun showError(){}

        fun showNoData()
        fun showData(it: List<TdsStatement>)

        fun setFiscalYearSpinner(arrayList: ArrayList<String>)
        fun setMonthSP(it: List<MonthData>)


    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getTDSStatement(month:String,year:String)
        fun getFiscalYear()

    }

}
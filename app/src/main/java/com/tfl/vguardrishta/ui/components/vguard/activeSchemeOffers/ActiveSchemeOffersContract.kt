package com.tfl.vguardrishta.ui.components.vguard.activeSchemeOffers

import com.tfl.vguardrishta.models.Category
import com.tfl.vguardrishta.models.DownloadData
import com.tfl.vguardrishta.ui.base.BaseContract

/**
 * Created by Shanmuka on 5/9/2019.
 */
interface ActiveSchemeOffersContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView()

        fun hideKeyboard()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)


        fun showNoData()

        fun setDownLoadsToAdapter(list: List<DownloadData>)
        fun openFile(it: DownloadData)


    }

    interface Presenter : BaseContract.Presenter<View> {

        fun getActiveSchemeOffers()
        fun openSchemesFile(it: DownloadData)
    }
}
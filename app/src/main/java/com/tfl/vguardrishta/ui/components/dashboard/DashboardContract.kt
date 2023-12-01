package com.tfl.vguardrishta.ui.components.dashboard

import android.app.Activity
import com.tfl.vguardrishta.ui.base.BaseContract

/**
 * Created by Shanmuka on 3/21/2019.
 */
interface DashboardContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun getActivity(): Activity

    }

    interface Presenter : BaseContract.Presenter<View>
}
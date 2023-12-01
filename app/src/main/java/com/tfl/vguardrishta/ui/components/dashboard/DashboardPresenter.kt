package com.tfl.vguardrishta.ui.components.dashboard

import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject

/**
 * Created by Shanmuka on 3/21/2019.
 */
class DashboardPresenter @Inject constructor() : BasePresenter<DashboardContract.View>(),
    DashboardContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

}
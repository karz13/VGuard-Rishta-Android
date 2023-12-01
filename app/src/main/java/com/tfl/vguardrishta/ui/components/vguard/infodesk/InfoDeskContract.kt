package com.tfl.vguardrishta.ui.components.vguard.infodesk

import com.tfl.vguardrishta.ui.base.BaseContract
import com.tfl.vguardrishta.models.SchemeImages

interface InfoDeskContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView() {}

        fun showProgress() {}

        fun stopProgress() {}

        fun showToast(toast: String) {}

        fun showError(){}
        fun showInfoDeskBanner(it: List<SchemeImages>?)


    }

    interface Presenter : BaseContract.Presenter<View> {


        fun getInfoDeskBanners()
    }

}
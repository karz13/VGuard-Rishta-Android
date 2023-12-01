package com.tfl.vguardrishta.ui.components.vguard.whats_new

import com.tfl.vguardrishta.ui.base.BaseContract
import com.tfl.vguardrishta.models.WhatsNew

interface WhatsNewContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView() {}

        fun showProgress() {}

        fun stopProgress() {}

        fun showToast(toast: String) {}

        fun showError(){}
        fun showWhatsNew(it: List<WhatsNew>?)
        fun showNoData()


    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getWhatsNew()


    }

}
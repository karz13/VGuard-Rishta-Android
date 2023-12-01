package com.tfl.vguardrishta.ui.components.vguard.schemes

import com.tfl.vguardrishta.ui.base.BaseContract
import com.tfl.vguardrishta.models.SchemeImages

interface SchemeAndOffersContract {

    interface View : BaseContract.View {

        fun initUI()

        fun finishView() {}

        fun showProgress() {}

        fun stopProgress() {}

        fun showToast(toast: String) {}

        fun showError(){}
        fun showSchemes(it: List<SchemeImages>?)


    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getSchemesBanners()


    }

}
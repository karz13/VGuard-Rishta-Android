package com.tfl.vguardrishta.ui.components.vguard.specialComboOffers

import com.tfl.vguardrishta.models.SpecialSchemes
import com.tfl.vguardrishta.ui.base.BaseContract
import kotlin.jvm.internal.Intrinsics


interface SpecialComboOffersContract {

    interface Presenter : BaseContract.Presenter<View> {
        fun getSpecialOffers()
    }

    interface View : BaseContract.View {
        fun finishView()
        fun initUI()
        fun loadCards(list: List<SpecialSchemes>)
        fun showError()
        fun showProgress()
        fun showToast(str: String)
        fun stopProgress()


        object DefaultImpls {
            fun finishView(view: View?) {}
            fun showProgress(view: View?) {}
            fun stopProgress(view: View?) {}
            fun showToast(view: View?, toast: String?) {
                Intrinsics.checkNotNullParameter(toast, "toast")
            }

            fun showError(view: View?) {}
        }
    }
}
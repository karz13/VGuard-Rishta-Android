package com.tfl.vguardrishta.ui.components.vguard.fragment.profile

import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.ui.base.BaseContract
import java.util.*

interface RishtaUserProfileContract {
    interface View : BaseContract.View {

        fun initUI()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showRishtaUser(it: VguardRishtaUser)

    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getProfile()


    }

}
package com.tfl.vguardrishta.ui.components.logIn

import android.app.Activity
import com.tfl.vguardrishta.ui.base.BaseContract

/**
 * Created by Shanmuka on 19-11-2018.
 */
interface LogInContract {

    interface View : BaseContract.View {

        fun initUI()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun errorEnterUserName()

        fun errorEnterPassword()

        fun getActivity(): Activity

        fun navigateToHome()

        fun finishView()

        fun redirectToPlayStore()

        fun navigateToChangePassword()

    }

    interface Presenter : BaseContract.Presenter<View> {

        fun onLoginClicked(userName: String, password: String, isRemember: Boolean)


    }

}
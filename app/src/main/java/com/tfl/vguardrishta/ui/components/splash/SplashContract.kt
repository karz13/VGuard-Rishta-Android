package com.tfl.vguardrishta.ui.components.splash

import com.tfl.vguardrishta.ui.base.BaseContract

/**
 * Created by Shanmuka on 16-1-2019.
 */
interface SplashContract {

    interface View : BaseContract.View {

        fun initUI()

        fun navigateToSignInScreen()

        fun finishView()

        fun navigateToHome()

        fun navigateToChangePassword()

    }

    interface Presenter : BaseContract.Presenter<View> {

        fun moveToSignInScreen()

    }
}
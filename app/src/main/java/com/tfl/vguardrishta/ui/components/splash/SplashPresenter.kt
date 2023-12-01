package com.tfl.vguardrishta.ui.components.splash

import android.content.Context
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.PrefUtil
import javax.inject.Inject

/**
 * Created by Shanmuka on 16-1-2019.
 */
class SplashPresenter @Inject constructor(private val context: Context) :
    BasePresenter<SplashContract.View>(),
    SplashContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun moveToSignInScreen() {
        if (PrefUtil(context).getIsLoggedIn()) {
            getView()?.navigateToHome()
        } else {
            getView()?.navigateToSignInScreen()
        }
//        getView()?.navigateToSignInScreen()
        getView()?.finishView()
    }
}
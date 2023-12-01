package com.tfl.vguardrishta.ui.components.splash

import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.withDelay
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.logIn.LogInActivity
import com.tfl.vguardrishta.ui.components.vguard.home.RishtaHomeActivity
import javax.inject.Inject

/**
 * Created by Shanmuka on 16-1-2019.
 */
class SplashActivity : BaseActivity<SplashContract.View, SplashContract.Presenter>(),
    SplashContract.View {

    @Inject
    lateinit var splashPresenter: SplashPresenter

    override fun initUI() {
        supportActionBar!!.hide()
        withDelay(1500) {
            splashPresenter.moveToSignInScreen()
        }
    }

    override fun initPresenter() = splashPresenter

    override fun injectDependencies() = getApplicationComponent().inject(this)

    override fun getLayoutResId() = R.layout.activity_splash

    override fun navigateToSignInScreen() = launchActivity<LogInActivity> { }

    override fun navigateToHome() = launchActivity<RishtaHomeActivity> { }

    override fun navigateToChangePassword() {

    }

    override fun finishView() = finish()


}
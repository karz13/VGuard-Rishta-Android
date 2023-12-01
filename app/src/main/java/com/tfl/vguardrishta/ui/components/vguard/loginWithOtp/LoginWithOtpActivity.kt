package com.tfl.vguardrishta.ui.components.vguard.loginWithOtp

import android.view.MenuItem
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.replaceFragment
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Constants.LOGIN_WITH_OTP_FRAGMENT
import javax.inject.Inject

/**
 * Created by Shanmuka on 3/21/2019.
 */
class LoginWithOtpActivity : BaseActivity<LoginWithOtpContract.View, LoginWithOtpContract.Presenter>(),
    LoginWithOtpContract.View {

    @Inject
    lateinit var loginWithOtpPresenter: LoginWithOtpPresenter

    override fun initUI() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        replaceFragment(LoginWithOtpFragment(), R.id.content, LOGIN_WITH_OTP_FRAGMENT)
    }

    override fun initPresenter() = loginWithOtpPresenter

    override fun injectDependencies() = getApplicationComponent().inject(this)

    override fun getLayoutResId() = R.layout.activity_login_with_otp

    override fun finishView() = finish()

    override fun hideKeyboard() = AppUtils.hideKeyboard(this)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

}
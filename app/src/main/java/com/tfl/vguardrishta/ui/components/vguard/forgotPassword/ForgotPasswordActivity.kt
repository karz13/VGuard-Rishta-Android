package com.tfl.vguardrishta.ui.components.vguard.forgotPassword

import android.view.MenuItem
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.replaceFragment
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.Constants.FORGOT_PASSWORD_FRAGMENT
import javax.inject.Inject

/**
 * Created by Shanmuka on 3/21/2019.
 */
class ForgotPasswordActivity : BaseActivity<ForgotPasswordContract.View, ForgotPasswordContract.Presenter>(), ForgotPasswordContract.View {

    @Inject
    lateinit var forgotPasswordPresenter: ForgotPasswordPresenter

    override fun initUI() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        replaceFragment(ForgotPasswordFragment(), R.id.content, FORGOT_PASSWORD_FRAGMENT)
    }

    override fun initPresenter() = forgotPasswordPresenter

    override fun injectDependencies() = getApplicationComponent().inject(this)

    override fun getLayoutResId() = R.layout.activity_forgot_password

    override fun finishView() = finish()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

}
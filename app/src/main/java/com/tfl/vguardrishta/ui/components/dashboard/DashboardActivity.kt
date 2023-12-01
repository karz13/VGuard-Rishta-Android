package com.tfl.vguardrishta.ui.components.dashboard

import android.app.Activity
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Progress
import javax.inject.Inject

/**
 * Created by Shanmuka on 3/21/2019.
 */
class DashboardActivity : BaseActivity<DashboardContract.View, DashboardContract.Presenter>(), DashboardContract.View {

    @Inject
    lateinit var dashboardPresenter: DashboardPresenter

    private lateinit var progress: Progress

    override fun initUI() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        progress = Progress(this, R.string.please_wait)
    }

    override fun initPresenter() = dashboardPresenter

    override fun injectDependencies() = getApplicationComponent().inject(this)

    override fun getLayoutResId() = R.layout.activity_splash

    override fun finishView() = finish()

    override fun getActivity(): Activity = this

    override fun showProgress() = progress.show()

    override fun stopProgress() = progress.dismiss()

    override fun showToast(toast: String) = AppUtils.showToast(this, toast)
}
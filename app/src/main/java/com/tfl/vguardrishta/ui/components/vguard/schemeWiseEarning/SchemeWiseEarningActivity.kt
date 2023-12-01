package com.tfl.vguardrishta.ui.components.vguard.schemeWiseEarning

import android.view.View
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.SchemeWiseEarning
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.activity_prod_wise_earning.*
import kotlinx.android.synthetic.main.v_activity_redeem_points.customToolbar
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class SchemeWiseEarningActivity :
    BaseActivity<SchemeWiseEarningContract.View, SchemeWiseEarningContract.Presenter>(),
    SchemeWiseEarningContract.View, View.OnClickListener {

    @Inject
    lateinit var schemeWiseEarningPresenter: SchemeWiseEarningPresenter
    private lateinit var schemeWiseEarningAdapter: SchemeWiseEarningAdapter


    private lateinit var progress: Progress

    override fun initPresenter(): SchemeWiseEarningContract.Presenter {
        return schemeWiseEarningPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_scheme_wise_earning
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.scheme_wise_earning), "")
        progress = Progress(this, R.string.please_wait)
        schemeWiseEarningAdapter = SchemeWiseEarningAdapter {}
        rcvProdWiseEarning.adapter = schemeWiseEarningAdapter
        ivBack.setOnClickListener(this)
        schemeWiseEarningPresenter.getEarningDetails()
    }

    override fun showEarningDetails(list: List<SchemeWiseEarning>) {
        tvNoData.visibility = View.GONE
        schemeWiseEarningAdapter.mList = list
        schemeWiseEarningAdapter.tempList = list
        schemeWiseEarningAdapter.notifyDataSetChanged()
    }

    override fun showNoData() {
        tvNoData.visibility = View.VISIBLE
    }

    override fun showProgress() {
        progress.show()
    }

    override fun stopProgress() {
        progress.dismiss()
    }

    override fun showToast(toast: String) {
        toast(toast)
    }

    override fun showError() {
        toast(resources.getString(R.string.something_wrong))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

}
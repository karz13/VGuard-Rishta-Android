package com.tfl.vguardrishta.ui.components.vguard.scanHistory

import android.view.View
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.CouponResponse
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.activity_scan_code_history.*
import kotlinx.android.synthetic.main.activity_ticket_history.tvNoData
import kotlinx.android.synthetic.main.v_activity_redeem_points.customToolbar
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class ScanCodeHistoryActivity :
    BaseActivity<ScanCodeHistoryContract.View, ScanCodeHistoryContract.Presenter>(),
    ScanCodeHistoryContract.View, View.OnClickListener {

    @Inject
    lateinit var scanCodeHistoryPresenter: ScanCodeHistoryPresenter
    private lateinit var scanHistoryAdapter: ScanHistoryAdapter
    private var mType: String? = null

    private lateinit var progress: Progress

    override fun initPresenter(): ScanCodeHistoryContract.Presenter {
        return scanCodeHistoryPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_scan_code_history
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.uniquie_code_history), "")
        progress = Progress(this, R.string.please_wait)
        mType = intent?.getStringExtra("type")
        scanHistoryAdapter = ScanHistoryAdapter {}
        rcvScanHistory.adapter = scanHistoryAdapter
        ivBack.setOnClickListener(this)
        if (!mType.isNullOrEmpty() && mType.equals("airCooler"))
            scanCodeHistoryPresenter.getAirCoolerScanCodeHistory()
        else
            scanCodeHistoryPresenter.getTicketHistory()
    }

    override fun showScanHistory(it: List<CouponResponse>) {
        tvNoData.visibility = View.GONE
        scanHistoryAdapter.mList = it
        scanHistoryAdapter.tempList = it
        scanHistoryAdapter.notifyDataSetChanged()
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
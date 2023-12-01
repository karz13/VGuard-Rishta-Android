package com.tfl.vguardrishta.ui.components.vguard.redemptionHistory

import android.view.View
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.RedemptionHistory
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.trackRedemption.TrackRedemptionActivity
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.v_activity_redemption_history.*
import kotlinx.android.synthetic.main.v_activity_whats_new.customToolbar
import kotlinx.android.synthetic.main.v_activity_whats_new.tvNoData
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class RedemptionHistoryActivity : BaseActivity<RedemptionHistoryContract.View, RedemptionHistoryContract.Presenter>(),
    RedemptionHistoryContract.View, View.OnClickListener {

    private lateinit var progress: Progress
    private var mType: String? = null

    @Inject
    lateinit var redemptionHistoryPresenter: RedemptionHistoryPresenter

    private lateinit var redemptionHistoryAdapter: RedemptionHistoryAdapter
    override fun initPresenter(): RedemptionHistoryContract.Presenter {
        return redemptionHistoryPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.v_activity_redemption_history
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.redemption_history), "")
        ivBack.setOnClickListener(this)
        progress = Progress(this, R.string.please_wait)
        mType = intent?.getStringExtra("type")
        redemptionHistoryAdapter = RedemptionHistoryAdapter { presenter?.trackRedemtion(it) }
        rcvRedemptionHistory.adapter = redemptionHistoryAdapter
        if (!mType.isNullOrEmpty() && mType.equals("airCooler"))
            redemptionHistoryPresenter.getHistory("airCooler")
        else
            redemptionHistoryPresenter.getHistory("")
    }

    override fun showTrackRedemption(orderId: String?) {
        if (orderId != null) {
            launchActivity<TrackRedemptionActivity> { }
        }
    }

    override fun showRedeemptionHistory(it: List<RedemptionHistory>) {
        tvNoData.visibility = View.GONE
        redemptionHistoryAdapter.mList = it
        redemptionHistoryAdapter.tempList = it
        redemptionHistoryAdapter.type = mType
        redemptionHistoryAdapter.notifyDataSetChanged()
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
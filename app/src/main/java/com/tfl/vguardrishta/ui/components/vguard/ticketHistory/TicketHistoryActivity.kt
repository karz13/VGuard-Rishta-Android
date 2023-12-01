package com.tfl.vguardrishta.ui.components.vguard.ticketHistory

import android.view.View
import com.bumptech.glide.Glide
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.TicketType
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.activity_ticket_history.*
import kotlinx.android.synthetic.main.username_info.*
import kotlinx.android.synthetic.main.v_activity_redeem_points.customToolbar
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class TicketHistoryActivity :
    BaseActivity<TicketHistoryContract.View, TicketHistoryContract.Presenter>(),
    TicketHistoryContract.View, View.OnClickListener {

    @Inject
    lateinit var ticketHistoryPresenter: TicketHistoryPresenter
    private lateinit var ticketHistoryAdapter: TicketHistoryAdapter


    private lateinit var progress: Progress

    override fun initPresenter(): TicketHistoryContract.Presenter {
        return ticketHistoryPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_ticket_history
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.ticket_history), "")
        progress = Progress(this, R.string.please_wait)
        ticketHistoryAdapter = TicketHistoryAdapter {}
        rcvTicketHistory.adapter = ticketHistoryAdapter
        ivBack.setOnClickListener(this)
        val vu = CacheUtils.getRishtaUser()
        tvDisplayName.text = vu.name
        tvUserCode.text = vu.userCode
        var selfie = vu.kycDetails.selfie
        selfie = AppUtils.getSelfieUrl() + selfie
        Glide.with(this).load(selfie)
            .placeholder(R.drawable.ic_v_guards_user).into(ivUserImage)

        ticketHistoryPresenter.getTicketHistory()
    }

    override fun showNoData() {
        tvNoData.visibility = View.VISIBLE
    }

    override fun showTicketHistory(it: List<TicketType>?) {
        showTicketsHistory(it as ArrayList<TicketType>)
        tvNoData.visibility = View.GONE
    }

    private fun showTicketsHistory(ticketList: ArrayList<TicketType>) {
        ticketHistoryAdapter.mList = ticketList
        ticketHistoryAdapter.tempList = ticketList
        ticketHistoryAdapter.notifyDataSetChanged()
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
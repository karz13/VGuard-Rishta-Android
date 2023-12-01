package com.tfl.vguardrishta.ui.components.vguard.paytm

import android.view.View
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.ProductDetail
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.ActivityFinishListener
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Progress
import com.tfl.vguardrishta.utils.Constants
import kotlinx.android.synthetic.main.activity_paytm_transfer.*
import kotlinx.android.synthetic.main.v_activity_redeem_points.customToolbar
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class PaytmTransferActivity :
    BaseActivity<PaytmTransferContract.View, PaytmTransferContract.Presenter>(),
    PaytmTransferContract.View,
    View.OnClickListener, ActivityFinishListener {
    private var defaultTracker: Tracker? = null
    private var mType: String? = null

    @Inject
    lateinit var paytmTransferPresenter: PaytmTransferPresenter

    private lateinit var progress: Progress

    override fun initPresenter(): PaytmTransferContract.Presenter {
        return paytmTransferPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_paytm_transfer
    }

    override fun initUI() {
        defaultTracker = (application as App).getDefaultTracker()

        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.paytm_transfer), "")
        progress = Progress(this, R.string.please_wait)
        hideKeyBoard()
        ivBack.setOnClickListener(this)
//        btnProceed.alpha = 0.4f
//        btnProceed.isEnabled = false
//        btnProceed.isClickable = false
        llPaytmCheck.setOnClickListener(this)
        btnProceed.setOnClickListener(this)
        val rishtaUser = CacheUtils.getRishtaUser()
        val pointsSummary = rishtaUser.pointsSummary
        tvPointsBalance.text = pointsSummary?.pointsBalance ?: "0.00"
        tvRedeemedPoints.text = pointsSummary?.redeemedPoints ?: "0.00"
        cbPaytm.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                btnProceed.alpha = 1f
                btnProceed.isEnabled = true
                btnProceed.isClickable = true
            } else {
                btnProceed.alpha = 0.4f
                btnProceed.isEnabled = false
                btnProceed.isClickable = false
            }
        }

        mType = intent?.getStringExtra("type")

    }

    override fun onResume() {
        super.onResume()
        defaultTracker?.setScreenName("Paytm Transfer")
        defaultTracker?.setPage("Paytm Transfer")
        defaultTracker?.setTitle("Paytm Transfer")
        defaultTracker?.send(HitBuilders.ScreenViewBuilder().build())
        intializeAirCoolerPayTmPage()

    }

    private fun intializeAirCoolerPayTmPage()
    {
        if(mType==Constants.AIRCOOLER_SCREEN){
            rateConversion.text = getString(R.string.one_point_one_inr_aircooler)
            enter_points_to_be_redeemed_below.text = getString(R.string.enter_stars_to_be_redeemed_below)
            etPoints.hint  = getString(R.string.enter_stars)
            ll_paytm_top.visibility = View.GONE
            etMobileNo.setText(CacheUtils.getRishtaUser().mobileNo)
            etMobileNo.setKeyListener(null);
        }
    }
    override fun hideKeyBoard() = AppUtils.hideKeyboard(this)

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

            R.id.llPaytmCheck -> {
                cbPaytm.isChecked = !cbPaytm.isChecked
                if (cbPaytm.isChecked) {
                    btnProceed.alpha = 1f
                    btnProceed.isEnabled = true
                    btnProceed.isClickable = true
                } else {
                    btnProceed.alpha = 0.4f
                    btnProceed.isEnabled = false
                    btnProceed.isClickable = false
                }
            }

            R.id.btnProceed -> {
                    getInputs()
            }
        }
    }

    private fun getInputs() {
        val points = etPoints.text.toString()
        val mobile = etMobileNo.text.toString();
        paytmTransferPresenter.validateAndTransfer(points, mobile,mType)
    }

    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialogLongText(layoutInflater, this, message)
    }

    override fun showMsgDialogWithFinish(message: String) {
        AppUtils.showErrorDialogWithFinish(layoutInflater, this, message, this)
    }

    override fun setPaytmProductId(it: ProductDetail?) {

    }

    override fun finishView() {
        this.finish()
    }
}
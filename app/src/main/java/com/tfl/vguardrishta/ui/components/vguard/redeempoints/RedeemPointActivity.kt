package com.tfl.vguardrishta.ui.components.vguard.redeempoints

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.bankTransfer.BankTransferActivity
import com.tfl.vguardrishta.ui.components.vguard.paytm.PaytmTransferActivity
import com.tfl.vguardrishta.ui.components.vguard.redeemproducts.RedeemProductsActivity
import com.tfl.vguardrishta.ui.components.vguard.redemptionHistory.RedemptionHistoryActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.fragment_rishta_home.*
import kotlinx.android.synthetic.main.help_details_layout.*
import kotlinx.android.synthetic.main.v_activity_redeem_points.*
import kotlinx.android.synthetic.main.v_activity_redeem_points.tvPointsBalance
import kotlinx.android.synthetic.main.v_activity_redeem_points.tvRedeemedPoints
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class RedeemPointActivity : BaseActivity<RedeemPointContract.View, RedeemPointContract.Presenter>(),
    RedeemPointContract.View, View.OnClickListener {

    @Inject
    lateinit var redeemPointPresenter: RedeemPointPresenter

    private lateinit var progress: Progress

    override fun initPresenter(): RedeemPointContract.Presenter {
        return redeemPointPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.v_activity_redeem_points
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.redeem_points), "")
        progress = Progress(this, R.string.please_wait)
        llRedeemProduct.setOnClickListener(this)
        ivBack.setOnClickListener(this)
        llEgiftCards.setOnClickListener(this)
        setPointsUi()
        llPaytmTransfer.setOnClickListener(this)
        llBankTransfer.setOnClickListener(this)
        llRedemptionHistory.setOnClickListener(this)
        llTrackRedeemption.setOnClickListener(this)

        tvContactNo.setOnClickListener {
            AppUtils.showPhoneDialer(this!!, tvContactNo.text.toString())
        }

        tvSupportMail.setOnClickListener {
            AppUtils.showComposeMail(this!!, tvSupportMail.text.toString())
        }

        tvWhatsappNo.setOnClickListener {
            AppUtils.launchWhatsApp(this!!, tvWhatsappNo.text.toString())
        }

        if (CacheUtils.getRishtaUserType() == Constants.RET_USER_TYPE)
        {

        }
        else {
            tsdDeducted.visibility = View.INVISIBLE
            tvTdsPoints.visibility = View.INVISIBLE
        }
    }

    private fun setPointsUi() {
        val rishtaUser = CacheUtils.getRishtaUser()
        val pointsSummary = rishtaUser.pointsSummary
        tvPointsBalance.text = "${pointsSummary?.pointsBalance ?: 0.0}"
        tvRedeemedPoints.text = "${pointsSummary?.redeemedPoints ?: 0.0}"
        tvTdsPoints.text = "${pointsSummary?.tdsPoints ?: 0.0}"

        if (rishtaUser.egvEnabled == 0) {
            disableEgv()
        } else {
            enableEgv()
        }
    }

    private fun enableEgv() {
        tvEgvLabel.setTextColor(getColor(R.color.black))
        ivEgvImg.setImageResource(R.drawable.ic_egift_cards)
        ivEgvImg.setColorFilter(resources.getColor(R.color.transparent_white))
        ivEgvImg.colorFilter = null
    }

    private fun disableEgv() {
        DrawableCompat.setTint(
            ivEgvImg.drawable,
            ContextCompat.getColor(this, R.color.grey)
        );
        tvEgvLabel.setTextColor(getColor(R.color.grey))
    }

    override fun onResume() {
        super.onResume()
        redeemPointPresenter.getUserDetails()

    }

    override fun showUserDetails() {
        setPointsUi()
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
            R.id.llRedeemProduct -> {
                AppUtils.showErrorDialog(layoutInflater, this, getString(R.string.coming_soon))
//                launchActivity<RedeemProductsActivity> {
//                    putExtra("isGift", false)
//
//                }
            }

            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.llPaytmTransfer -> {
                launchActivity<PaytmTransferActivity> { }
            }

            R.id.llBankTransfer -> {
                launchActivity<BankTransferActivity> { }
            }

            R.id.llEgiftCards -> {
                val egvEnabled = CacheUtils.getRishtaUser().egvEnabled
                if (egvEnabled == 0) {
                    AppUtils.showErrorDialog(
                        layoutInflater,
                        this,
                        getString(R.string.coming_soon)
                    )
                } else {
                    launchActivity<RedeemProductsActivity> {
                        putExtra("isGift", true)
                    }
                }
            }

            R.id.llRedemptionHistory -> {
                launchActivity<RedemptionHistoryActivity> { }
            }

            R.id.llTrackRedeemption -> {
                AppUtils.showErrorDialog(layoutInflater, this, getString(R.string.coming_soon))

            }
        }
    }
}
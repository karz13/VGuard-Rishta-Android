package com.tfl.vguardrishta.ui.components.vguard.airCooler

import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.databinding.VActivityAirCoolerBinding
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.PointsSummary
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.redeemproducts.RedeemProductsActivity
import com.tfl.vguardrishta.ui.components.vguard.redemptionHistory.RedemptionHistoryActivity
import com.tfl.vguardrishta.ui.components.vguard.scanCode.ScanCodeActivity
import com.tfl.vguardrishta.ui.components.vguard.scanHistory.ScanCodeHistoryActivity
import com.tfl.vguardrishta.ui.components.vguard.paytm.PaytmTransferActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.help_details_layout.*
import kotlinx.android.synthetic.main.v_activity_air_cooler.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class AirCoolerActivity : BaseActivity<AirCoolerContract.View, AirCoolerContract.Presenter>(),
    AirCoolerContract.View, View.OnClickListener {

    @Inject
    lateinit var airCoolerPresenter: AirCoolerPresenter
    lateinit var binding: VActivityAirCoolerBinding
    private lateinit var progress: Progress

    override fun initPresenter(): AirCoolerContract.Presenter {
        return airCoolerPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.v_activity_air_cooler
    }

    override fun initUI() {
        binding = DataBindingUtil.setContentView(
            this, R.layout.v_activity_air_cooler
        )
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.redeem_points), "")
        progress = Progress(this, R.string.please_wait)
        ivBack.setOnClickListener(this)
        setUserDetails()

        if(CacheUtils.getRishtaUser().airCoolerEnabled == Constants.AIR_COOLER_ENABLED)
        {
            llProductRegister.setOnClickListener(this)
            llSchemeDetails.setOnClickListener(this)
            llRedeemProduct.setOnClickListener(this)
            product_registration_text.setTextColor(resources.getColor(R.color.black))
            redeemstar_text.setTextColor(resources.getColor(R.color.black))
            scheme_details_text.setTextColor(resources.getColor(R.color.black))
        }
        else if(CacheUtils.getRishtaUser().airCoolerEnabled == Constants.AIR_COOLER_DISABLED) {
            llPaytmTransfer.setOnClickListener(this)
            paytm_text.setTextColor(resources.getColor(R.color.black))
        }

        llNumberOfScans.setOnClickListener(this)
        llRedeemedPoints.setOnClickListener(this)

        tvContactNo.setOnClickListener {
            AppUtils.showPhoneDialer(this!!, tvContactNo.text.toString())
        }

        tvSupportMail.setOnClickListener {
            AppUtils.showComposeMail(this!!, tvSupportMail.text.toString())
        }

        tvWhatsappNo.setOnClickListener {
            AppUtils.launchWhatsApp(this!!, tvWhatsappNo.text.toString())
        }
        airCoolerPresenter.getAirCoolerPointSummary()
    }

    private fun setUserDetails() {
        val vu = CacheUtils.getRishtaUser()
        tvDisplayName.text = vu.name?.toUpperCase()
        tvUserCode.text = vu.userCode
        var selfie = vu.kycDetails.selfie
        selfie = AppUtils.getSelfieUrl() + selfie
        Glide.with(this).load(selfie)
            .placeholder(R.drawable.ic_v_guards_user).into(ivUserImage)
    }

    private fun setPointsUi(pointsSummary: PointsSummary) {
        binding.starPoints = pointsSummary
    }


    override fun onResume() {
        super.onResume()
        airCoolerPresenter.getAirCoolerPointSummary()

    }

    override fun showUserDetails(pointsSummary: PointsSummary) {
        setPointsUi(pointsSummary)
    }

    override fun showSchemeDetails(it: String) {
        AppUtils.openPDFWithBaseUrl(this, it)
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
            R.id.llProductRegister -> {
                launchActivity<ScanCodeActivity> {
                    putExtra("type", "airCooler")
                }
            }
            R.id.llSchemeDetails -> {
                //  AppUtils.openPDFWithUrl(this, ApiService.pdf)
                airCoolerPresenter.getAirCoolerSchemeDetails()
                /* launchActivity<ProductWiseOffersDetailActivity> {
                     putExtra("cat_id", "6")
                 }*/
                // launchActivity<SchemesAndOffersActivity>()
            }
            R.id.llRedeemProduct -> {
                launchActivity<RedeemProductsActivity> {
                    putExtra("isGift", false)
                    putExtra("type", "airCooler")
                }
            }

            R.id.llPaytmTransfer -> {
               /* launchActivity<PaytmTransferActivity> {
                    putExtra("type", Constants.AIRCOOLER_SCREEN)
                }*/

                AppUtils.showErrorDialogLongText(
                    layoutInflater,
                    this,
                    "Dear member, the Air cooler amount is now added in the main balance. you can redeem it after clicking on redeem points tab."
                )
            }


            R.id.llNumberOfScans -> {
                launchActivity<ScanCodeHistoryActivity> {
                    putExtra("type", "airCooler")
                }

            }
            R.id.llRedeemedPoints -> {
                launchActivity<RedemptionHistoryActivity> {
                    putExtra("type", "airCooler")
                }

            }

        }
    }
}
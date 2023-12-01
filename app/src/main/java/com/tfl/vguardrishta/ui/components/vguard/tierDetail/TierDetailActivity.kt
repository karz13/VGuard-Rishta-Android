package com.tfl.vguardrishta.ui.components.vguard.tierDetail

import android.view.View
import com.bumptech.glide.Glide
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.extensions.visible
import com.tfl.vguardrishta.models.TierPoints
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.help_details_layout.*
import kotlinx.android.synthetic.main.username_info.*
import kotlinx.android.synthetic.main.v_activity_tier_detail.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class TierDetailActivity : BaseActivity<TierDetailContract.View, TierDetailContract.Presenter>(),
    TierDetailContract.View, View.OnClickListener {

    private lateinit var progress: Progress

    @Inject
    lateinit var tierDetailPresenter: TierDetailPresenter

    override fun initPresenter(): TierDetailContract.Presenter {
        return tierDetailPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.v_activity_tier_detail
    }

    override fun initUI() {
        progress = Progress(this, R.string.please_wait)
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.tier_club), "")
        ivBack.setOnClickListener(this)

        tvContactNo.setOnClickListener {
            AppUtils.showPhoneDialer(this, tvContactNo.text.toString())
        }

        tvSupportMail.setOnClickListener {
            AppUtils.showComposeMail(this, tvSupportMail.text.toString())
        }

        tvWhatsappNo.setOnClickListener {
            AppUtils.launchWhatsApp(this, tvWhatsappNo.text.toString())
        }
        tierDetailPresenter.getTierDetails()

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

    override fun showTierDetail(tierPoints: TierPoints?) {
        tvTotalClubPoints.text = tierPoints?.totalClubPoints
        tvBasePoints.text = tierPoints?.basePoints
        tvSchemePoints.text = tierPoints?.schemePoints
        tvFirstScan.text = getString(R.string.first_scan_date) + "" + tierPoints?.firstScan
        tvProfession.text = tierPoints?.profession
        var selfie = tierPoints?.selfie
        selfie =AppUtils.getSelfieUrl() + selfie
        Glide.with(this).load(selfie)
            .placeholder(R.drawable.ic_v_guards_user).into(ivUserImage)
        tvDisplayName.text = tierPoints?.name
        tvUserCode.text = tierPoints?.rishtaId

        if (tierPoints?.clubType == "SILVER CLUB") {
            ivClubType.setImageResource(R.drawable.ic_silver_club)
            ivClubType.visible = true
        } else if (tierPoints?.clubType == "PLATINUM CLUB") {
            ivClubType.visible = true
            ivClubType.setImageResource(R.drawable.ic_platinum_club)
        } else if (tierPoints?.clubType == "GOLD CLUB") {
            ivClubType.setImageResource(R.drawable.ic_gold_club)
            ivClubType.visible = true
        }
        llViewPdf.setOnClickListener {
            AppUtils.openPDFWithBaseUrl(this,tierPoints?.pdfPath)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }
}
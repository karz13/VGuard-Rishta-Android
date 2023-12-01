package com.tfl.vguardrishta.ui.components.vguard.referralCode

import android.content.Intent
import android.view.View
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.activity_product_catalog.*
import kotlinx.android.synthetic.main.activity_referral_code.*
import kotlinx.android.synthetic.main.v_activity_redeem_points.customToolbar
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class ReferralCodeActivity :
    BaseActivity<ReferralCodeContract.View, ReferralCodeContract.Presenter>(),
    ReferralCodeContract.View, View.OnClickListener {


    private lateinit var progress: Progress

    @Inject
    lateinit var referralCodePresenter: ReferralCodePresenter

    private fun setCustomActionBar() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.referral_code), "")
        ivBack.setOnClickListener(this)
    }

    override fun initPresenter(): ReferralCodeContract.Presenter {
        return referralCodePresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_referral_code
    }

    override fun initUI() {
        setCustomActionBar()
        progress = Progress(this, R.string.please_wait)
        val rishtaUser = CacheUtils.getRishtaUser()
        tvReferralCode.text = rishtaUser.referralCode

        btnShareCode.setOnClickListener {
            shareReferralCode(rishtaUser.referralCode)
        }
    }

    private fun shareReferralCode(referralCode: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain";
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        intent.putExtra(android.content.Intent.EXTRA_TEXT,getString(R.string.share_subject)+" \n"+ referralCode);
        startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
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

    override fun showNoData() {
        tvNoData.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }
}
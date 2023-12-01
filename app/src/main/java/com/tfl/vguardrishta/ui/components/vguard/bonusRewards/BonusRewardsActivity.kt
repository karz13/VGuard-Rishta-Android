package com.tfl.vguardrishta.ui.components.vguard.bonusRewards

import android.view.View
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.CouponResponse
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.v_activity_bonus_rewards.*
import kotlinx.android.synthetic.main.v_activity_whats_new.customToolbar
import kotlinx.android.synthetic.main.v_activity_whats_new.tvNoData
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class BonusRewardsActivity :
    BaseActivity<BonusRewardsContract.View, BonusRewardsContract.Presenter>(),
    BonusRewardsContract.View, View.OnClickListener {

    private lateinit var progress: Progress

    @Inject
    lateinit var bonusRewardsPresenter: BonusRewardsPresenter

    private lateinit var bonusRewardsAdapter: BonusRewardsAdapter
    override fun initPresenter(): BonusRewardsContract.Presenter {
        return bonusRewardsPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.v_activity_bonus_rewards
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.your_rewards), "")
        ivBack.setOnClickListener(this)

        progress = Progress(this, R.string.please_wait)
        bonusRewardsAdapter = BonusRewardsAdapter { }
        rcvBonus.adapter = bonusRewardsAdapter

        bonusRewardsPresenter.getBonusRewards()

    }

    override fun showBonusRewards(it: List<CouponResponse>) {
        tvNoData.visibility = View.GONE
        bonusRewardsAdapter.mList = it
        bonusRewardsAdapter.tempList = it
        bonusRewardsAdapter.notifyDataSetChanged()

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
package com.tfl.vguardrishta.ui.components.vguard.activeSchemeOffers

import android.view.View
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.visible
import com.tfl.vguardrishta.models.DownloadData
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.activity_active_scheme_offers.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class ActiveSchemeOffersActivity :
    BaseActivity<ActiveSchemeOffersContract.View, ActiveSchemeOffersContract.Presenter>(),
    ActiveSchemeOffersContract.View,
    View.OnClickListener {
    private lateinit var progress: Progress
    private lateinit var activeSchemeAdapter: ActiveSchemesAdapter

    @Inject
    lateinit var activeSchemeOffersPresenter: ActiveSchemeOffersPresenter


    override fun openFile(it: DownloadData) {
        AppUtils.openPDFWithBaseUrl(this, it.fileName)

    }

    private fun setCustomActionBar() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.active_scheme_offers), "")
        ivBack.setOnClickListener(this)
    }

    override fun initUI() {
        setCustomActionBar()
        progress = Progress(this, R.string.please_wait)
        activeSchemeAdapter = ActiveSchemesAdapter {activeSchemeOffersPresenter.openSchemesFile(it) }
        rcvSchemeOffers.adapter = activeSchemeAdapter
        activeSchemeOffersPresenter.getActiveSchemeOffers()

    }

    override fun finishView() = finish()

    override fun hideKeyboard() = AppUtils.hideKeyboard(this)

    override fun showProgress() = progress.show()

    override fun stopProgress() = progress.dismiss()

    override fun showToast(toast: String) = AppUtils.showToast(this, toast)

    override fun showNoData() {
        stopProgress()
        rcvSchemeOffers.visible = false
        tvNoData.visible = true
    }

    override fun setDownLoadsToAdapter(list: List<DownloadData>) {
        activeSchemeAdapter.mList = list
        activeSchemeAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    override fun initPresenter(): ActiveSchemeOffersContract.Presenter {
        return activeSchemeOffersPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_active_scheme_offers
    }

}
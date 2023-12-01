package com.tfl.vguardrishta.ui.components.vguard.welfare

import android.view.View
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.extensions.visible
import com.tfl.vguardrishta.models.DownloadData
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.activity_welfare.*
import kotlinx.android.synthetic.main.v_activity_redeem_points.customToolbar
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class WelfareActivity : BaseActivity<WelfareContract.View, WelfareContract.Presenter>(),
    View.OnClickListener, WelfareContract.View {

    private lateinit var progress: Progress

    @Inject
    lateinit var welfarePresenter: WelfarePresenter

    lateinit var welfareAdapter: WelfareAdapter
    private fun setCustomActionBar() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.welfare), "")
        ivBack.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    override fun initPresenter(): WelfareContract.Presenter {
        return welfarePresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_welfare
    }

    override fun initUI() {
        setCustomActionBar()
        progress = Progress(this, R.string.please_wait)

        welfareAdapter = WelfareAdapter {
            AppUtils.openPDFWithBaseUrl(this,it.fileName)
        }
        rcvWelfare.adapter = welfareAdapter
        welfarePresenter.getWelfare()
    }

    override fun showProgress() {
        progress.show()
    }

    override fun stopProgress() {
        progress.dismiss()
    }

    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun showNoData() {
        tvNoData.visible = true
    }

    override fun showWelfare(it: List<DownloadData>) {
        welfareAdapter.mList = it
        welfareAdapter.notifyDataSetChanged()
    }


}
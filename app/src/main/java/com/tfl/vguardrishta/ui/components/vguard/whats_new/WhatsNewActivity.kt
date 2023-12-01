package com.tfl.vguardrishta.ui.components.vguard.whats_new

import android.view.View
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.WhatsNew
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.dailyWinner.DailyWinnerActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.v_activity_whats_new.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import java.util.*
import javax.inject.Inject

class WhatsNewActivity : BaseActivity<WhatsNewContract.View, WhatsNewContract.Presenter>(),
    WhatsNewContract.View, View.OnClickListener {

    private lateinit var progress: Progress

    @Inject
    lateinit var whatsNewPresenter: WhatsNewPresenter

    private lateinit var whatsNewAdapter: WhatsNewAdapter
    override fun initPresenter(): WhatsNewContract.Presenter {
        return whatsNewPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.v_activity_whats_new
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.what_s_new), "")
        ivBack.setOnClickListener(this)

        progress = Progress(this, R.string.please_wait)
        whatsNewAdapter = WhatsNewAdapter {
            if (it.imagePath == "daily_winner") {
                launchActivity<DailyWinnerActivity> { }
            } else if (it.detailDesc.equals("youtube", false)) {
                AppUtils.openPDFWithUrl(this, it.imagePath)
            } else {
                openPdf(it.imagePath)
            }
        }
        rcvWhatsNew.adapter = whatsNewAdapter

        whatsNewPresenter.getWhatsNew()
//        val araylit = arrayListOf<WhatsNew>()
//        for (i in 1..10) {
//            val whatsNew = WhatsNew()
//            araylit.add(whatsNew)
//        }
//
//        setWhatsNewAdaper(araylit)
    }

    private fun openPdf(imagePath: String?) {
        AppUtils.openPDFWithBaseUrl(this, imagePath)
    }

    override fun showWhatsNew(it: List<WhatsNew>?) {
        tvNoData.visibility = View.GONE
        setWhatsNewAdaper(it as ArrayList<WhatsNew>)
    }

    override fun showNoData() {
        tvNoData.visibility = View.VISIBLE
    }

    private fun setWhatsNewAdaper(whatsNewList: ArrayList<WhatsNew>) {
        whatsNewAdapter.mList = whatsNewList
        whatsNewAdapter.tempList = whatsNewList
        whatsNewAdapter.notifyDataSetChanged()
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
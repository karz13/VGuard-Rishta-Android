package com.tfl.vguardrishta.ui.components.vguard.dailyWinner

import android.view.View
import android.widget.AdapterView
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.DailyWinner
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Progress
import com.tfl.vguardrishta.utils.SpinnerUtils
import kotlinx.android.synthetic.main.activity_daily_winner.*
import kotlinx.android.synthetic.main.activity_daily_winner.customToolbar
import kotlinx.android.synthetic.main.activity_update_kyc.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import java.util.*
import javax.inject.Inject

class DailyWinnerActivity : BaseActivity<DailyWinnerContract.View, DailyWinnerContract.Presenter>(),
    DailyWinnerContract.View, View.OnClickListener {

    private var userSelectId: Boolean = false
    private lateinit var progress: Progress

    @Inject
    lateinit var dailyWinnerPresenter: DailyWinnerPresenter

    private lateinit var dailyWinnerAdapter: DailyWinnerAdapter
    override fun initPresenter(): DailyWinnerContract.Presenter {
        return dailyWinnerPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_daily_winner
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.daily_winner), "")
        ivBack.setOnClickListener(this)
        setDailyDatesSpinnerListener()
        progress = Progress(this, R.string.please_wait)
        dailyWinnerAdapter = DailyWinnerAdapter { }
        rcvDailyWinner.adapter = dailyWinnerAdapter

        dailyWinnerPresenter.getDailyWinnerDates()

        val dailyWinner = DailyWinner()
        dailyWinnerPresenter.getDailyWinner(dailyWinner)
//        val araylit = arrayListOf<WhatsNew>()
//        for (i in 1..10) {
//            val whatsNew = WhatsNew()
//            araylit.add(whatsNew)
//        }
//
//        setWhatsNewAdaper(araylit)
    }

    override fun setDailyWInnerSp(it: List<DailyWinner>) {
        spDate?.adapter =
            CustomSpinner(
                this,
                android.R.layout.simple_list_item_1,
                it.toTypedArray(),
                Constants.WINEER_DATES
            )
    }

    private fun setDailyDatesSpinnerListener() {

        spDate.setOnTouchListener { view, motionEvent ->
            userSelectId = true
            return@setOnTouchListener false
        }

        spDate.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!userSelectId) {
                    return
                }
                val dw = parent?.selectedItem as DailyWinner
                dailyWinnerPresenter.getDailyWinner(dw)
            }
        }
    }


    private fun openPdf(imagePath: String?) {
        AppUtils.openPDFWithBaseUrl(this, imagePath)
    }

    override fun setDailyWinners(it: List<DailyWinner>) {
        tvNoData.visibility = View.GONE
        setWhatsNewAdaper(it as ArrayList<DailyWinner>)
    }

    override fun showNoData() {
        tvNoData.visibility = View.VISIBLE
    }

    private fun setWhatsNewAdaper(dwList: ArrayList<DailyWinner>) {
        dailyWinnerAdapter.mList = dwList
        dailyWinnerAdapter.tempList = dwList
        dailyWinnerAdapter.notifyDataSetChanged()
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
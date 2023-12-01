package com.tfl.vguardrishta.ui.components.vguard.tdsStatement

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.extensions.visible
import com.tfl.vguardrishta.models.MonthData
import com.tfl.vguardrishta.models.TdsData
import com.tfl.vguardrishta.models.TdsStatement
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Progress
import com.tfl.vguardrishta.utils.SpinnerUtils
import kotlinx.android.synthetic.main.activity_tds_statement.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class TDSstatementActivity : BaseActivity<TDSstatementContract.View, TDSstatementContract.Presenter>(),
    View.OnClickListener, TDSstatementContract.View {

    private lateinit var progress: Progress
    private var year=""
    @Inject
    lateinit var tDSStatementPresenter: TDSstatementPresenter

    lateinit var tDSAdapter: TDSstatementAdapter
    private fun setCustomActionBar() {
        setSupportActionBar(toolbar_main)
        customToolbarTds.updateToolbar("", getString(R.string.tds_statement), "")
        ivBack.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    override fun initPresenter(): TDSstatementContract.Presenter {
        return tDSStatementPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_tds_statement
    }

    override fun initUI() {
        setCustomActionBar()
        progress = Progress(this, R.string.please_wait)

        tDSAdapter = TDSstatementAdapter {}
        rcvTdsStatement.adapter = tDSAdapter

        tDSStatementPresenter.getFiscalYear()
        tDSStatementPresenter.getMonth()

        spMonth.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                AppUtils.hideKeyboard(this@TDSstatementActivity!!)
                var month = parent?.selectedItem as MonthData
                year=spFiscaly.selectedItem.toString()
                tDSStatementPresenter.getTDSStatement(month.id!!,year)
            }
        }
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

    override fun showData(it: List<TdsStatement>) {
        tDSAdapter.mList = it
        tDSAdapter.notifyDataSetChanged()
    }

    override fun setFiscalYearSpinner(arrayList: ArrayList<String>) {
        val spinnerArrayAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayList.toArray()
        )
        spFiscaly.adapter =spinnerArrayAdapter
    }

    override fun setMonthSP(it: List<MonthData>) {
        spMonth?.adapter =
            CustomSpinner(
                this,
                R.layout.custom_simple_spinner_item2,
                it.toTypedArray(),
                Constants.MONTHS
            )
    }


}
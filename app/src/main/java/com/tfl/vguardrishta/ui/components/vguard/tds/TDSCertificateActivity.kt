package com.tfl.vguardrishta.ui.components.vguard.tds

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.extensions.visible
import com.tfl.vguardrishta.models.DownloadData
import com.tfl.vguardrishta.models.TdsData
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.welfare.WelfareAdapter
import com.tfl.vguardrishta.ui.components.vguard.welfare.WelfareContract
import com.tfl.vguardrishta.ui.components.vguard.welfare.WelfarePresenter
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Progress
import com.tfl.vguardrishta.utils.SpinnerUtils
import kotlinx.android.synthetic.main.activity_tds.*
import kotlinx.android.synthetic.main.activity_welfare.*
import kotlinx.android.synthetic.main.activity_welfare.rcvWelfare
import kotlinx.android.synthetic.main.activity_welfare.tvNoData
import kotlinx.android.synthetic.main.v_activity_redeem_points.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class TDSCertificateActivity : BaseActivity<TDSCertificateContract.View, TDSCertificateContract.Presenter>(),
    View.OnClickListener, TDSCertificateContract.View {

    private lateinit var progress: Progress
    private  var accessmentYear :String =""
    @Inject
    lateinit var tDSCertificatePresenter: TDSCertificatePresenter

    lateinit var tDSAdapter: TDSAdapter
    private fun setCustomActionBar() {
        setSupportActionBar(toolbar_main)
        customToolbarTds.updateToolbar("", getString(R.string.tds_certificate), "")
        ivBack.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    override fun initPresenter(): TDSCertificateContract.Presenter {
        return tDSCertificatePresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_tds
    }

    override fun initUI() {
        setCustomActionBar()
        progress = Progress(this, R.string.please_wait)

        tDSAdapter = TDSAdapter {
            AppUtils.openPDFWithBaseUrl(this,it.path)
        }
        rcvWelfare.adapter = tDSAdapter

        tDSCertificatePresenter.getAccessmentYear()

        spAy.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                AppUtils.hideKeyboard(this@TDSCertificateActivity!!)
                accessmentYear = parent?.selectedItem as String
                tDSCertificatePresenter.getTDSCertificate(accessmentYear)
              /*  if (position != 0) {
                    AppUtils.hideKeyboard(this@TDSCertificateActivity!!)
                    accessmentYear = parent?.selectedItem as String
                    tDSCertificatePresenter.getTDSCertificate(accessmentYear)
                }*/
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

    override fun showWelfare(it: List<TdsData>) {
        tDSAdapter.mList = it
        tDSAdapter.notifyDataSetChanged()
    }

    override fun setAccementSpinner(arrayList: ArrayList<String>) {
        val spinnerArrayAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayList.toArray()
        )
        spAy.adapter =spinnerArrayAdapter
    }


}
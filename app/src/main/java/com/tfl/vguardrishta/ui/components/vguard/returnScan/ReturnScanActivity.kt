package com.tfl.vguardrishta.ui.components.vguard.returnScan

import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import com.google.android.gms.vision.barcode.Barcode
import com.tfl.barcode_reader.BarcodeReaderActivity
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.v_activity_scan_code.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject


class ReturnScanActivity : BaseActivity<ReturnScanContract.View, ReturnScanContract.Presenter>(),
    ReturnScanContract.View {

    @Inject
    lateinit var returnScanPresenter: ReturnScanPresenter

    private lateinit var progress: Progress
    private val barCodeActivityRequest = 1208

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.scan_code), "")
        progress = Progress(this, R.string.please_wait)
        onClicks()
    }

    override fun initPresenter() = returnScanPresenter

    override fun injectDependencies() = getApplicationComponent().inject(this)

    override fun getLayoutResId() = R.layout.activity_return_scan

    override fun finishView() = finish()

    override fun showProgress() = progress.show()

    override fun stopProgress() = progress.dismiss()

    override fun showToast(message: String) = AppUtils.showToast(this, message)

    override fun showLongToast(message: String) = AppUtils.showLongToast(this, message)


    private fun onClicks() {

    }

    override fun clearCoupon() {
        etBarcode.setText("")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return
        if (requestCode == barCodeActivityRequest && data != null) {
            val barcode =
                data.getParcelableExtra<Barcode>(BarcodeReaderActivity.KEY_CAPTURED_BARCODE)
            if (!barcode?.rawValue.isNullOrEmpty()) {
                etBarcode.setText("")
                val code = barcode?.rawValue
//                if (isRetailerCoupon!!) {
//                    etBarcode.setText(code)
//                    sendBarcode()
//                } else {
//                    if (code?.length!! > 19) {
//                        val substringCode = barcode.rawValue.substring(9)
//                        etBarcode.setText(substringCode)
//                    } else {
//                        etBarcode.setText(code)
//                    }
//                }
                etBarcode.setText(code)

            } else {
                showToast(getString(R.string.invalid_barcode))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
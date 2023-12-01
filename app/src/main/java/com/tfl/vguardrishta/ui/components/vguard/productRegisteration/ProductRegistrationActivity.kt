package com.tfl.vguardrishta.ui.components.vguard.productRegisteration

import android.view.View
import com.google.android.gms.analytics.Tracker
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.replaceFragment
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.registerProduct.AddCustomerDetFragment
import com.tfl.vguardrishta.ui.components.vguard.scanCode.ScanCodeActivity
import com.tfl.vguardrishta.utils.ActivityFinishListener
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.v_activity_redeem_points.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class ProductRegistrationActivity : BaseActivity<ProductRegistrationContract.View, ProductRegistrationContract.Presenter>(),
    ProductRegistrationContract.View,
    View.OnClickListener, ActivityFinishListener {
    private var defaultTracker: Tracker? = null

    @Inject
    lateinit var productRegistrationPresenter: ProductRegistrationPresenter

    private lateinit var progress: Progress

    override fun initPresenter(): ProductRegistrationContract.Presenter {
        return productRegistrationPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_register_product
    }

    private fun setCustomActionBar() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.product_registration), "")
        ivBack.setOnClickListener(this)
    }

    override fun initUI() {
        setCustomActionBar()
        progress = Progress(this, R.string.please_wait)
        replaceFragment(AddCustomerDetFragment(), R.id.content, "")
    }


    override fun hideKeyBoard() = AppUtils.hideKeyboard(this)

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

            R.id.btnProceed -> {
                getInputs()
            }
        }
    }

    private fun getInputs() {

    }

    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialog(layoutInflater, this, message)
    }

    override fun showMsgDialogWithFinish(message: String) {
        AppUtils.showErrorDialogWithFinish(layoutInflater, this, message, this)
    }

    override fun navigateToScanProduct() {
        launchActivity<ScanCodeActivity>()
    }


    override fun finishView() {
        this.finish()
    }

}
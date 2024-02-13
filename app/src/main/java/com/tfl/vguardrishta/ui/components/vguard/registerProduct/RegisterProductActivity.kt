package com.tfl.vguardrishta.ui.components.vguard.registerProduct

import android.view.View
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.addFragment
import com.tfl.vguardrishta.extensions.replaceFragment
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.v_activity_redeem_points.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class RegisterProductActivity :
    BaseActivity<RegisterProductContract.View, RegisterProductContract.Presenter>(),
    RegisterProductContract.View, View.OnClickListener {
    private lateinit var progress: Progress

    @Inject
    lateinit var registerProductPresenter: RegisterProductPresenter

    private fun setCustomActionBar() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.product_registration), "")
        ivBack.setOnClickListener(this)
    }

    override fun initPresenter(): RegisterProductContract.Presenter {
        return registerProductPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_register_product
    }

    override fun initUI() {
        setCustomActionBar()
        progress = Progress(this, R.string.please_wait)
        navigateToCustomer();
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

    override fun proceedToNextPage() {
        TODO("Not yet implemented")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    fun showRegisterProductFragment() {
        replaceFragment(RegisterProductFragment(), R.id.content)
    }

    fun navigateToCustomer() {
        addFragment(AddCustomerDetFragment(), R.id.content)
    }
}
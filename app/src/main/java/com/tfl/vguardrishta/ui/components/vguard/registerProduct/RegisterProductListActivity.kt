package com.tfl.vguardrishta.ui.components.vguard.registerProduct

import android.util.Log
import android.view.View
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.ProductDetail
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.productRegisteration.ProductRegistrationActivity
import com.tfl.vguardrishta.ui.components.vguard.scanCode.ScanCodeActivity
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.activity_register_product_list.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class RegisterProductListActivity :
    BaseActivity<RegisterProductContract.View, RegisterProductContract.Presenter>(),
    RegisterProductContract.View, View.OnClickListener {

    private lateinit var progress: Progress

    private var selectedPD: ProductDetail? = null

    @Inject
    lateinit var registerProductPresenter: RegisterProductPresenter
    lateinit var registerProductAdapter: RegisterProductAdapter

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
        return R.layout.activity_register_product_list
    }

    override fun initUI() {
        progress = Progress(this!!, R.string.please_wait)
        setCustomActionBar()
        registerProductAdapter =RegisterProductAdapter { productDetail -> onItemClick(productDetail) }
        rcvProducts.adapter = registerProductAdapter
        registerProductPresenter.getRetProductCategories()

    }

    private fun onItemClick(productDetail: ProductDetail) {
        selectedPD = productDetail
        launchActivity<ProductRegistrationActivity> { }
    }

    override fun onResume() {
        super.onResume()
        if (registerProductAdapter.mList.isNotEmpty()) {
            setProductsCatalog(registerProductAdapter.mList)
        }
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

    override fun showNoData() {
        tvNoData.visibility = View.VISIBLE
    }

    override fun setProductsCatalog(list: List<ProductDetail>) {
        registerProductAdapter.mList = list
        registerProductAdapter.tempList = list
        registerProductAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }
}
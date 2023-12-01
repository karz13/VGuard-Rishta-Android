package com.tfl.vguardrishta.ui.components.vguard.productCatalog

import android.view.View
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.ProductDetail
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.activity_product_catalog.*
import kotlinx.android.synthetic.main.v_activity_redeem_points.customToolbar
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class ProductCatalogActivity :
    BaseActivity<ProductCatalogContract.View, ProductCatalogContract.Presenter>(),
    ProductCatalogContract.View, View.OnClickListener {


    private lateinit var progress: Progress

    @Inject
    lateinit var productCatalogPresenter: ProductCatalogPresenter
    lateinit var productCatalogAdapter: ProductCatalogAdapter

    private fun setCustomActionBar() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.v_guard_product_catalog), "")
        ivBack.setOnClickListener(this)
    }

    override fun initPresenter(): ProductCatalogContract.Presenter {
        return productCatalogPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_product_catalog
    }

    override fun initUI() {
        setCustomActionBar()
        progress = Progress(this, R.string.please_wait)
        productCatalogAdapter = ProductCatalogAdapter { }
        rcvProducts.adapter = productCatalogAdapter

        productCatalogPresenter.getProductCatalog()
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    override fun setProductsCatalog(list: List<ProductDetail>) {
        productCatalogAdapter.mList = list
        productCatalogAdapter.tempList = list
        productCatalogAdapter.notifyDataSetChanged()
    }
}
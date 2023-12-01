package com.tfl.vguardrishta.ui.components.vguard.productWiseOffers

import android.view.View
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.ProductWiseOffers
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.productWiseOfferDetail.ProductWiseOffersDetailActivity
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.v_activity_bonus_rewards.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class ProductWiseOffersActivity :
    BaseActivity<ProductWiseOfferContract.View, ProductWiseOfferContract.Presenter>(),
    ProductWiseOfferContract.View, View.OnClickListener {

    private lateinit var progress: Progress

    @Inject
    lateinit var productWisePresenter: ProductWiseOfferPresenter

    private lateinit var productWiseOfferAdapter: ProductWiseOfferAdapter
    override fun initPresenter(): ProductWiseOfferContract.Presenter {
        return productWisePresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.v_activity_product_wise_offers
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.product_wise_offers), "")
        ivBack.setOnClickListener(this)

        progress = Progress(this, R.string.please_wait)
        productWiseOfferAdapter = ProductWiseOfferAdapter { productWisePresenter.getProductWiseOffersDetail(it)}
        rcvBonus.adapter = productWiseOfferAdapter
        productWisePresenter.getProductWiseOffers()
    }

    override fun navigateToOfferDetails(categoryId: String?) {
         launchActivity<ProductWiseOffersDetailActivity> {
             putExtra("cat_id",categoryId)
         }
    }

    override fun showNoData() {
        tvNoData.visibility = View.VISIBLE
    }

    override fun showProductWiseOffers(it: List<ProductWiseOffers>) {
        tvNoData.visibility = View.GONE
        productWiseOfferAdapter.mList = it
        productWiseOfferAdapter.tempList = it
        productWiseOfferAdapter.notifyDataSetChanged()
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
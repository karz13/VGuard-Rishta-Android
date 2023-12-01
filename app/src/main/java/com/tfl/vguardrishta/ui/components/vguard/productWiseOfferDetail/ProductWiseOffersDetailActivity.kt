package com.tfl.vguardrishta.ui.components.vguard.productWiseOfferDetail

import android.view.View
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.ProductWiseOffersDetail
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.activity_prod_wise_offer_detail.*
import kotlinx.android.synthetic.main.v_activity_redeem_points.customToolbar
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class ProductWiseOffersDetailActivity :
    BaseActivity<ProductWiseOffersDetailContract.View, ProductWiseOffersDetailContract.Presenter>(),
    ProductWiseOffersDetailContract.View, View.OnClickListener {

    @Inject
    lateinit var productWiseOffersDetailPresenter: ProductWiseOffersDetailPresenter
    private lateinit var productWiseOffersDetailAdapter: ProductWiseOffersDetailAdapter


    private lateinit var progress: Progress

    override fun initPresenter(): ProductWiseOffersDetailContract.Presenter {
        return productWiseOffersDetailPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_prod_wise_offer_detail
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.product_wise_offers), "")
        val catId = intent?.getStringExtra("cat_id")
        val rishtaUser = CacheUtils.getRishtaUser()
        if (catId == "5" && rishtaUser.roleId == Constants.RET_USER_TYPE) {
            tvBenifitAmout.text = getString(R.string.stars)
        }
        if(rishtaUser.roleId == Constants.RET_USER_TYPE||rishtaUser.roleId == Constants.INF_USER_TYPE)
        {
            tvMaterialCode.text=getString(R.string.material_code)
        }
        progress = Progress(this, R.string.please_wait)
        productWiseOffersDetailAdapter = ProductWiseOffersDetailAdapter {}
        rcvProdWiseOfferDetail.adapter = productWiseOffersDetailAdapter
        ivBack.setOnClickListener(this)
        productWiseOffersDetailPresenter.getDetails(catId)
    }

    override fun showOfferDetails(it: List<ProductWiseOffersDetail>?) {
        showTicketsHistory(it as ArrayList<ProductWiseOffersDetail>)
        tvNoData.visibility = View.GONE
    }

    override fun showNoData() {
        tvNoData.visibility = View.VISIBLE
    }


    private fun showTicketsHistory(list: ArrayList<ProductWiseOffersDetail>) {
        productWiseOffersDetailAdapter.mList = list
        productWiseOffersDetailAdapter.tempList = list
        productWiseOffersDetailAdapter.notifyDataSetChanged()
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
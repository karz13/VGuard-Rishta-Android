package com.tfl.vguardrishta.ui.components.vguard.redeemproducts

import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.extensions.visible
import com.tfl.vguardrishta.extensions.withDelay
import com.tfl.vguardrishta.models.Category
import com.tfl.vguardrishta.models.ProductDetail
import com.tfl.vguardrishta.models.ProductSort
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.viewcart.ViewCartActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.fragment_new_user_kyc_details.*
import kotlinx.android.synthetic.main.v_activity_redeem_products.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class RedeemProductsActivity :
    BaseActivity<RedeemProductsContract.View, RedeemProductsContract.Presenter>(),
    RedeemProductsContract.View, View.OnClickListener {
    private lateinit var progress: Progress
    private var defaultTracker: Tracker? = null

    private var userSelectCat: Boolean = false
    private var userSelectSort: Boolean = false
    private var isGift: Boolean = false
    private var mType: String? = null

    @Inject
    lateinit var redeemProductsPresenter: RedeemProductsPresenter

    lateinit var productListAdapter: ProductListAdapter

    override fun initPresenter(): RedeemProductsContract.Presenter {
        return redeemProductsPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.v_activity_redeem_products
    }

    override fun initUI() {
        defaultTracker = (application as App).getDefaultTracker()

        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.redeem_products), "")
        showSort()
        isGift = intent?.getBooleanExtra("isGift", false)!!
        mType = intent?.getStringExtra("type")
        if (isGift) {
            customToolbar.updateToolbar("", getString(R.string.e_gift_cards), "")
        }
        if (!mType.isNullOrEmpty() && mType.equals("airCooler")) {
            customToolbar.updateToolbar("", getString(R.string.redeem_stars), "")
            spProdCategory.visible = false
            flViewCart.visible = false
            btnProceedCart.visible = false

        }
        btnProceedCart.alpha = 0.4f
        btnProceedCart.isEnabled = false
        btnProceedCart.isClickable = false

        CacheUtils.setProducts(arrayListOf())
        CacheUtils.setAirCoolerProduct(null)
        progress = Progress(this, R.string.please_wait)

        productListAdapter = ProductListAdapter { productDetails, pos ->
            if (!mType.isNullOrEmpty() && mType.equals("airCooler")) {
                productDetails.type = mType
                CacheUtils.setAirCoolerProduct(productDetails)
                launchActivity<ViewCartActivity> { }
            } else
                redeemProductsPresenter.setSelection(
                    productDetails,
                    pos,
                    productListAdapter.tempList as ArrayList<ProductDetail>,
                    productListAdapter.mList as ArrayList<ProductDetail>
                )
        }

        btnProceedCart.setOnClickListener(this)
        flViewCart.setOnClickListener(this)
        ivBack.setOnClickListener(this)


        spProdCategory.setOnTouchListener { view, motionEvent ->
            userSelectCat = true
            return@setOnTouchListener false
        }

        spProdCategory.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!userSelectCat) {
                    return
                }
                userSelectCat = false

                getProductList(mType)

            }
        }

        spSort.setOnTouchListener { view, motionEvent ->
            userSelectSort = true
            return@setOnTouchListener false
        }

        spSort.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!userSelectSort) {
                    return
                }
                userSelectSort = false
                getProductList(mType)
            }
        }
    }


    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialog(layoutInflater, this, message)
    }

    private fun getProductList(type: String?) {
        var cat: Category? = null
        if (mType.isNullOrEmpty())
            cat = spProdCategory.selectedItem as Category
        val ps = spSort.selectedItem as ProductSort

        redeemProductsPresenter.getProductList(cat, ps, type)
    }

    override fun showCartItems(it: List<ProductDetail>) {
        updateAddCartStatus(true)
    }


    override fun setProducts(prodList: ArrayList<ProductDetail>) {
        rcvProducts.layoutManager = GridLayoutManager(this, 2)
        rcvProducts.adapter = productListAdapter
        productListAdapter.mList = prodList
        productListAdapter.tempList = prodList
        productListAdapter.type = mType
        CacheUtils.setProducts(prodList)
        productListAdapter.notifyDataSetChanged()
        if (mType.isNullOrEmpty()) {
            withDelay(200) {
                redeemProductsPresenter.getCartItems()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (mType.isNullOrEmpty())
            redeemProductsPresenter.getCategory()
        else
            getProductList(mType)
        defaultTracker?.setScreenName("Redeem Products")
        defaultTracker?.setAppName("APP")
        defaultTracker?.send(HitBuilders.ScreenViewBuilder().build())
//        redeemProductsPresenter.getCartItems()
    }

    override fun updateProducts(
        mList: List<ProductDetail>,
        pos: Int,
        productDetail: ProductDetail
    ) {
        if (productDetail.isSelected) {
            tvCartCount.text = "1"
        } else {
            tvCartCount.text = "0"
        }
        productListAdapter.mList = mList
        productListAdapter.notifyDataSetChanged()
        rcvProducts.scrollToPosition(pos)
    }

    override fun updateAddCartStatus(selected: Boolean) {

        if (selected) {
            btnProceedCart.isClickable = true
            btnProceedCart.isEnabled = true
            btnProceedCart.alpha = 1f
            tvCartCount.text = "1"
            flViewCart.isClickable = true
            btnProceedCart.visibility = View.VISIBLE
        } else {
            btnProceedCart.isClickable = false
            btnProceedCart.isEnabled = true
            btnProceedCart.alpha = 0.4f
            tvCartCount.text = "0"
            flViewCart.isClickable = false
            btnProceedCart.visibility = View.GONE
        }
    }

    override fun showCategory(arrayList: ArrayList<Category>) {
        spProdCategory.adapter =
            CustomSpinner(
                this,
                R.layout.custom_simple_spinner_item,
                arrayList.toTypedArray(),
                Constants.CATEGORY
            )
//            val stateMaster = arrayList.find { it.prodCatId ==23L }
//            val index = arrayList.indexOf(stateMaster)
//            spProdCategory.setSelection(index)

        withDelay(200) {
            getProductList(mType)
        }
    }

    private fun getAllProductList() {
        val productSort = spSort.selectedItem as ProductSort
        if (isGift) {
            val category = Category()
            category.prodCatId = 23 //cat id 23 for gift card
            presenter?.getProductList(category, productSort, mType)
        } else {
            presenter?.getProductList(null, productSort, mType)
        }
    }

    fun showSort() {
        val sortList = CacheUtils.getSortList(this)
        spSort.adapter =
            CustomSpinner(
                this,
                R.layout.custom_simple_spinner_item,
                sortList.toTypedArray(),
                Constants.SORT
            )
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
            R.id.btnProceedCart -> {

                launchActivity<ViewCartActivity> { }
            }

            R.id.flViewCart -> {
                launchActivity<ViewCartActivity> { }

            }
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }
}
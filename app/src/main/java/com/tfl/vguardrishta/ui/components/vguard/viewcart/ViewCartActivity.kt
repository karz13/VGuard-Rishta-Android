package com.tfl.vguardrishta.ui.components.vguard.viewcart

import android.view.View
import android.widget.AdapterView
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.extensions.withDelay
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.v_activity_view_cart.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import java.util.*
import javax.inject.Inject

class ViewCartActivity : BaseActivity<ViewCartContract.View, ViewCartContract.Presenter>(),
    ViewCartContract.View, View.OnClickListener, ActivityFinishListener {

    private var mShippingAddress: ShippingAddress? = null
    private var otherShippingAddress: ShippingAddress = ShippingAddress()
    private lateinit var progress: Progress
    private var cartList = arrayListOf<ProductDetail>()
    private lateinit var cartProductListAdapter: CartProductListAdapter

    @Inject
    lateinit var viewCartPresenter: ViewCartPresenter

    override fun initPresenter(): ViewCartContract.Presenter {
        return viewCartPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.v_activity_view_cart
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.view_cart), "")
        ivBack.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
        progress = Progress(this, R.string.please_wait)
        cartProductListAdapter = CartProductListAdapter { viewCartPresenter.removeFromCart(it) }

        cvAddressView.visibility = View.GONE
        cbHome.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                cbOther.isChecked = false
            }
        }

        cbOther.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                cbHome.isChecked = false
                llOtherAddress.visibility = View.VISIBLE

                viewCartPresenter.getStates()
            } else {
                llOtherAddress.visibility = View.GONE
            }
        }
        setStatesSpinner(CacheUtils.getFirstState(this!!))
        setDistrictSpinner(CacheUtils.getFirstDistrict(this!!))
        setCitySpinner(CacheUtils.getFirstCity(this!!))
        val productDetails = CacheUtils.getAirCoolerProduct()
        if (productDetails != null)
            showCartItemsList(arrayListOf(productDetails))
        else
            viewCartPresenter.getCartItems()
    }

    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialog(layoutInflater, this, message)
    }

    override fun showErrorMsgWithFinish(message: String) {
        AppUtils.showErrorDialogWithFinish(layoutInflater, this, message, this)

    }

    override fun showEmptyList() {
        withDelay(200) {
            this.finish()
        }
    }

    override fun setStatesSpinner(arrayList: ArrayList<StateMaster>) {
        spState?.adapter =
            CustomSpinner(
                this!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.STATES
            )



        spState.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val stateMaster = parent?.selectedItem as StateMaster

                    viewCartPresenter.getDistrict(stateMaster.id)
                }
            }
        }
    }

    override fun setDistrictSpinner(arrayList: ArrayList<DistrictMaster>) {
        spDist?.adapter =
            CustomSpinner(
                this!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.DISTRICT
            )

        spDist.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val distmaster = parent?.selectedItem as DistrictMaster
                    viewCartPresenter.getCities(distmaster.id)
                }
            }
        }
    }

    override fun setCitySpinner(arrayList: ArrayList<CityMaster>) {
        spCity?.adapter =
            CustomSpinner(
                this!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.CITY
            )

        spCity.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val cityMaster = parent?.selectedItem as CityMaster
                }
            }
        }
    }

    override fun showCartItemsList(it: List<ProductDetail>) {
        cartList = it as ArrayList<ProductDetail>
        setProducts(it as ArrayList<ProductDetail>)

        val pointsSum = cartList.sumByDouble { it.points!! }
        tvSubTotal.text = "Subtotal ( ${cartList.size} Item)"
        var points = ""
        if (cartList.size == 1 && !cartList[0].type.isNullOrEmpty() && cartList[0].type == "airCooler")
            points = "$pointsSum Stars"
        else
            points = "$pointsSum Points"
        tvCartPoints.text = points
        withDelay(200) {
            viewCartPresenter.getShippingAddress()
        }
    }

    override fun setShippingAddress(it: ShippingAddress) {
        mShippingAddress = it
        tvHomeAddress.text =
            "${it.currentHomeFlatBlockNo ?: ""} " +
                    "${it.currentStreetColonyLocality ?: ""} " +
                    "${it.currentLandMark ?: ""} " +
                    "${it?.currentCity ?: ""} " +
                    "${it.currentState ?: ""} " +
                    "${it.currentPinCode ?: ""} " +
                    "${it.currentContactNo ?: ""}"
    }

    private fun setProducts(prodList: ArrayList<ProductDetail>) {
        rcvCartProducts.adapter = cartProductListAdapter
        cartProductListAdapter.mList = prodList
        cartProductListAdapter.tempList = prodList
        cartProductListAdapter.notifyDataSetChanged()
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

            R.id.btnSubmit -> {
                val isOther = cbOther.isChecked
                val isHome = cbHome.isChecked
                if (!isOther && !isHome) {
                    showToast(getString(R.string.please_select_shipping_address_home_or_other))
                    return
                }

                if (isOther) {
                    otherShippingAddress?.currentHomeFlatBlockNo = etDeliveryAddress.text.toString()
                    otherShippingAddress?.currentStreetColonyLocality =
                        etStreetLocality.text.toString()
                    otherShippingAddress?.currentLandMark = etLandmark.text.toString()

                    val stateMaster = spState.selectedItem as StateMaster
                    val cityMaster = spCity.selectedItem as CityMaster
                    val distMaster = spDist.selectedItem as DistrictMaster
                    otherShippingAddress?.currentState = stateMaster.stateName
                    otherShippingAddress?.currentStateId = stateMaster.id.toString()
                    otherShippingAddress?.currentDistId = distMaster.id.toString()
                    otherShippingAddress?.currentDist = distMaster.districtName.toString()
                    otherShippingAddress?.currentCityId = cityMaster.id.toString()
                    otherShippingAddress?.currentCity = cityMaster.cityName
                    otherShippingAddress?.currentPinCode = etPinCode.text.toString()

                }

                viewCartPresenter.submitProduct(
                    cartList,
                    mShippingAddress,
                    isOther,
                    otherShippingAddress
                )
            }
        }
    }


    override fun finishView() {
        this.finish()
    }

}
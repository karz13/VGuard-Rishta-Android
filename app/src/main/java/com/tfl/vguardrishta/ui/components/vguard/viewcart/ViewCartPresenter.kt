package com.tfl.vguardrishta.ui.components.vguard.viewcart

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.*
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import java.util.*
import javax.inject.Inject

class ViewCartPresenter @Inject constructor(
    val context: Context,
    val productOrderUseCase: ProductOrderUseCase,
    val cartItemsUseCase: GetCartItemsUseCase,
    val getShippingAddressUseCase: GetShippingAddressUseCase,
    val stateMasterUseCase: StateMasterUseCase,
    val cityMasterUseCase: CityMasterUseCase,
    val districtMasterUseCase: DistrictMasterUseCase,
    val removeFromCartUseCase: RemoveFromCartUseCase
) : BasePresenter<ViewCartContract.View>(), ViewCartContract.Presenter {


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun submitProduct(
        cartList: ArrayList<ProductDetail>,
        sAdd: ShippingAddress?,
        other: Boolean,
        otherShippingAddress: ShippingAddress?
    ) {
        if (cartList.isNotEmpty()) {
            val productDetail = cartList[0]
            val productOrder = ProductOrder()
            productOrder.productDetail = productDetail
            productOrder.shippingAddress = sAdd

            if (other) {

                if (otherShippingAddress?.currentHomeFlatBlockNo.isNullOrEmpty()) {
                    getView()?.showToast(context.getString(R.string.please_enter_del_address))
                    return
                }

                if (otherShippingAddress?.currentStreetColonyLocality.isNullOrEmpty()) {
                    getView()?.showToast(context.getString(R.string.please_enter_street))
                    return
                }


                if (otherShippingAddress?.currentStateId == "-1") {
                    getView()?.showToast(context.getString(R.string.please_select_state))
                    return
                }

                if (otherShippingAddress?.currentDistId == "-1") {
                    getView()?.showToast(context.getString(R.string.please_select_dist))
                    return
                }


                if (otherShippingAddress?.currentPinCode.isNullOrEmpty()) {
                    getView()?.showToast(context.getString(R.string.please_enter_pincode))
                    return
                }
                if (!AppUtils.isValidPinCode(otherShippingAddress?.currentPinCode!!)) {
                    getView()?.showToast(context.getString(R.string.enter_valid_pincode))
                    return
                }

                productOrder.shippingAddress = otherShippingAddress
            } else {
                productOrder.shippingAddress = sAdd
            }
            sendOrder(productOrder)
        }
    }

    override fun getStates() {
        disposables?.add(stateMasterUseCase.execute().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processStateMasters(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun processStateMasters(it: List<StateMaster>?) {
        val arrayList = CacheUtils.getFirstState(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        CacheUtils.setStates(arrayList)
        getView()?.setStatesSpinner(arrayList)
    }


    override fun getDistrict(id: Long?) {
        disposables?.add(districtMasterUseCase.execute(id!!).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processDistMaster(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun processCityMaster(it: List<CityMaster>?) {
        val arrayList = CacheUtils.getFirstCity(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        CacheUtils.setCities(arrayList)
        getView()?.setCitySpinner(arrayList)
    }


    private fun processDistMaster(it: List<DistrictMaster>) {
        val arrayList = CacheUtils.getFirstDistrict(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        CacheUtils.setDistricts(arrayList)
        getView()?.setDistrictSpinner(arrayList)
    }


    override fun getCities(id: Long?) {
        disposables?.add(cityMasterUseCase.execute(id!!).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processCityMaster(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun removeFromCart(it: ProductDetail) {
        disposables?.add(removeFromCartUseCase.execute(it).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null && it.code == 200) {
                    getView()?.showErrorMsgWithFinish(it.message)
                } else {
                    getView()?.showMsgDialog(it.message)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }


    private fun sendOrder(po: ProductOrder) {
        disposables?.add(productOrderUseCase.execute(po).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processResult(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun getCartItems() {
        disposables?.add(cartItemsUseCase.execute(Unit).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null && !it.isNullOrEmpty()) {
                    getView()?.showCartItemsList(it)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }


    override fun getShippingAddress() {
        disposables?.add(getShippingAddressUseCase.execute(Unit).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    getView()?.setShippingAddress(it)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun processResult(it: Status?) {
        if (it == null) {
            getView()?.showToast(context.getString(R.string.problem_occurred))
            return
        }
        if (it.code == 200) {
            getView()?.showErrorMsgWithFinish(it.message)
        } else if (it.code == 400) {
            getView()?.showMsgDialog(it.message)
        }
    }

}
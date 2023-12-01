package com.tfl.vguardrishta.ui.components.vguard.viewcart

import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.ui.base.BaseContract
import java.util.ArrayList

interface ViewCartContract {

    interface View : BaseContract.View {

        fun initUI()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showCartItemsList(it: List<ProductDetail>)
        fun setShippingAddress(it: ShippingAddress)
        fun setStatesSpinner(arrayList: ArrayList<StateMaster>)
        fun setDistrictSpinner(arrayList: ArrayList<DistrictMaster>)
        fun setCitySpinner(arrayList: ArrayList<CityMaster>)
        fun showEmptyList()
        fun showMsgDialog(message: String)
        fun showErrorMsgWithFinish(message: String)

    }

    interface Presenter : BaseContract.Presenter<View> {
        fun submitProduct(
            cartList: ArrayList<ProductDetail>,
            sAdd: ShippingAddress?,
            other: Boolean,
            otherShippingAddress: ShippingAddress?
        )

        fun getCartItems()

        fun getShippingAddress()
        fun getStates()
        fun getDistrict(id: Long?)
        fun getCities(id: Long?)
        fun removeFromCart(it: ProductDetail)


    }

}
package com.tfl.vguardrishta.ui.components.vguard.redeemproducts

import com.tfl.vguardrishta.models.Category
import com.tfl.vguardrishta.models.ProductDetail
import com.tfl.vguardrishta.models.ProductSort
import com.tfl.vguardrishta.ui.base.BaseContract
import kotlin.collections.ArrayList

interface RedeemProductsContract {

    interface View : BaseContract.View {

        fun initUI()


        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showCategory(arrayList: ArrayList<Category>)

        fun setProducts(prodList: ArrayList<ProductDetail>)
        fun updateProducts(
            tempList: List<ProductDetail>,
            pos: Int,
            productDetail: ProductDetail
        )

        fun updateAddCartStatus(selected: Boolean)
        fun showMsgDialog(message: String)
        fun showCartItems(it: List<ProductDetail>)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getCategory()


        fun setSelection(
            productDetail: ProductDetail,
            pos: Int,
            tempList: ArrayList<ProductDetail>,
            mList: ArrayList<ProductDetail>
        )


        fun addToCart(
            productDetail: ProductDetail,
            pos: Int,
            tempList: ArrayList<ProductDetail>,
            mList: ArrayList<ProductDetail>
        )

        fun getProductList(category: Category?, productSort: ProductSort?, type: String?)
        fun getCartItems()
    }

}
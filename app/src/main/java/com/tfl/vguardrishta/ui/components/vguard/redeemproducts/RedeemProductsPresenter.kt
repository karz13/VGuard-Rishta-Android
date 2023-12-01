package com.tfl.vguardrishta.ui.components.vguard.redeemproducts

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.*
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.Category
import com.tfl.vguardrishta.models.ProductDetail
import com.tfl.vguardrishta.models.ProductRequest
import com.tfl.vguardrishta.models.ProductSort
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.CacheUtils
import javax.inject.Inject

class RedeemProductsPresenter @Inject constructor(
    val context: Context,
    val categoryListUseCase: CategoryListUseCase,
    val productDetailListUseCase: ProductDetailListUseCase,
    val addToCartUseCase: AddToCartUseCase,
    val removeFromCartUseCase: RemoveFromCartUseCase,
    val cartItemsUseCase: GetCartItemsUseCase
) : BasePresenter<RedeemProductsContract.View>(), RedeemProductsContract.Presenter {


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getCategory() {

        disposables?.add(categoryListUseCase.execute().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    val firstCategory = CacheUtils.getFirstCategory(context)
                    firstCategory.addAll(it)
                    getView()?.showCategory(firstCategory as ArrayList<Category>)
                } else {
                    getView()?.showToast(context.getString(R.string.something_wrong))
                }
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
                    getView()?.showCartItems(it)
                } else {
                    getView()?.updateAddCartStatus(false)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun getProductList(
        category: Category?,
        productSort: ProductSort?,
        type: String?
    ) {
        val pr = ProductRequest()
        if (category != null) {
            pr.categoryId = category?.prodCatId.toString()
        }
        if (category != null && category.prodCatId == -1L) {
            pr.categoryId = ""
        }

        if (productSort != null) {
            pr.sortId = productSort?.sortId.toString()
        }
        pr.type = type
        disposables?.add(productDetailListUseCase.execute(pr).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    getView()?.setProducts(it as ArrayList<ProductDetail>)
                } else {
                    getView()?.showToast(context.getString(R.string.something_wrong))
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun setSelection(
        productDetail: ProductDetail,
        pos: Int,
        tempList: ArrayList<ProductDetail>,
        mList: ArrayList<ProductDetail>
    ) {
        if (!productDetail.isSelected) {
            addToCart(
                productDetail, pos, tempList,
                mList
            )
        } else {
            removeFromCart(
                productDetail, pos, tempList,
                mList
            )
        }


    }

    private fun removeFromCart(
        productDetail: ProductDetail,
        pos: Int,
        tempList: ArrayList<ProductDetail>,
        mList: ArrayList<ProductDetail>
    ) {

        disposables?.add(removeFromCartUseCase.execute(productDetail).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null && it.code == 200) {
                    getView()?.showMsgDialog(it.message)


                    //                    val final = arrayListOf<ProductDetail>()
//                    val tempArraylist = CacheUtils.getProducts()
                    productDetail.isSelected = !productDetail.isSelected
//                    final.addAll(tempArraylist)
                    mList[pos] = productDetail
                    getView()?.updateProducts(mList, pos, productDetail)
                    getView()?.updateAddCartStatus(false)
                    CacheUtils.refreshView(true)

                } else {
                    getView()?.showMsgDialog(it.message)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }


    override fun addToCart(
        productDetail: ProductDetail,
        pos: Int,
        tempList: ArrayList<ProductDetail>,
        mList: ArrayList<ProductDetail>
    ) {


        disposables?.add(addToCartUseCase.execute(productDetail).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null && it.code == 200) {

                    //                    val final = arrayListOf<ProductDetail>()
//                    val tempArraylist = CacheUtils.getProducts()
                    productDetail.isSelected = !productDetail.isSelected
//                    final.addAll(tempArraylist)
                    mList[pos] = productDetail
                    getView()?.updateProducts(mList, pos, productDetail)
                    getView()?.showMsgDialog(it.message)
                    getView()?.updateAddCartStatus(true)
                    CacheUtils.refreshView(true)

                } else {
                    getView()?.showMsgDialog(it.message)
                }
            }, {
                getView()?.showMsgDialog(context.getString(R.string.something_wrong))
            })
        )
    }


}
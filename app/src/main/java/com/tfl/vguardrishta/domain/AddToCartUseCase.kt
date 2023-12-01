package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.ProductDetail
import com.tfl.vguardrishta.models.Status
import io.reactivex.Single
import javax.inject.Inject

open class AddToCartUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<ProductDetail, Single<Status>>() {

    override fun execute(productDetail: ProductDetail): Single<Status> =
        dataRepository.addToCart(productDetail)

}
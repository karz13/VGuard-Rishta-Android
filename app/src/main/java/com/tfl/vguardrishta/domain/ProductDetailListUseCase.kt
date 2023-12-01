package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.ProductDetail
import com.tfl.vguardrishta.models.ProductRequest
import io.reactivex.Single
import javax.inject.Inject


class ProductDetailListUseCase @Inject constructor(private val dataRepository: DataRepository) :
UseCase<ProductRequest, Single<List<ProductDetail>>>() {

    override fun execute(pr: ProductRequest): Single<List<ProductDetail>> = dataRepository.getProductListing(pr)

}
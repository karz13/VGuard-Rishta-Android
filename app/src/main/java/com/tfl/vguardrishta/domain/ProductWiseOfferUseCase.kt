package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.ProductWiseOffers
import com.tfl.vguardrishta.models.ProductWiseOffersDetail
import io.reactivex.Single
import javax.inject.Inject


class ProductWiseOfferUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<String, Single<List<ProductWiseOffersDetail>>>() {

    override fun execute(parameters: String): Single<List<ProductWiseOffersDetail>> = dataRepository.getProductWiseOffersDetail(parameters)

    fun getProductWiseOffers(): Single<List<ProductWiseOffers>> {
        return dataRepository.getProductWiseOffers()
    }

}
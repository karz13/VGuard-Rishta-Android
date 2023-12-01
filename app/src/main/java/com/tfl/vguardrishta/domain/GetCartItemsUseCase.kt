package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.CityMaster
import com.tfl.vguardrishta.models.ProductDetail
import io.reactivex.Single
import javax.inject.Inject

class GetCartItemsUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<Unit, Single<List<ProductDetail>>>() {
    override fun execute(parameters: Unit): Single<List<ProductDetail>> {
         return dataRepository.getCartItems()
    }


}
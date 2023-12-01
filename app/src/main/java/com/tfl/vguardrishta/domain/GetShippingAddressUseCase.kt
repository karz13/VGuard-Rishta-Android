package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.ProductDetail
import com.tfl.vguardrishta.models.ShippingAddress
import io.reactivex.Single
import javax.inject.Inject

class GetShippingAddressUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<Unit, Single<ShippingAddress>>() {
    override fun execute(parameters: Unit): Single<ShippingAddress> {
        return dataRepository.getShippingAddress()
    }
}
package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.ProductDetail
import io.reactivex.Single
import javax.inject.Inject

class GetBankProductIdUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<Unit, Single<ProductDetail>>() {
    override fun execute(parameters: Unit): Single<ProductDetail> {
        return dataRepository.getBankProductId()
    }


}
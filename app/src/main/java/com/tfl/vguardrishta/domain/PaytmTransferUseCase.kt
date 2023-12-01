package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.ProductOrder
import com.tfl.vguardrishta.models.Status
import io.reactivex.Single
import javax.inject.Inject

open class PaytmTransferUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<ProductOrder, Single<Status>>() {

    override fun execute(po: ProductOrder): Single<Status> =
        dataRepository.paytmOrder(po)

    fun executeForAirCooler(po: ProductOrder): Single<Status> =
        dataRepository.paytmOrderForAirCooler(po)

}
package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.*
import io.reactivex.Single
import javax.inject.Inject

open class ProductOrderUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<ProductOrder, Single<Status>>() {

    override fun execute(po: ProductOrder): Single<Status> =
        dataRepository.orderProduct(po)

}
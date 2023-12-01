package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.Order
import com.tfl.vguardrishta.models.Status
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 5/8/2019.
 */
class OrderUploadUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<Order, Single<Status>>() {
    override fun execute(parameters: Order): Single<Status> = dataRepository.uploadOrder(parameters)
}
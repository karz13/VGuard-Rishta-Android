package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.Status
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 6/21/2019.
 */
class UpdateOrderUSeCase @Inject constructor(private val dataRepository: DataRepository) :
    TransactionCase<Unit, Single<Status>>() {

    override fun execute(id: Long): Single<Status> = dataRepository.updateOrder(id)

}
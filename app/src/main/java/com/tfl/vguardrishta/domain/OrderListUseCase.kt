package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.Order
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 6/20/2019.
 */
class OrderListUseCase @Inject constructor(private val dataRepository: DataRepository) :
    ListUseCase<Unit, Single<List<Order>>>() {

    override fun execute(): Single<List<Order>> = dataRepository.getOrders()

}
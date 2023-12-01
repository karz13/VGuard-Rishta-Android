package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.FilterObject
import com.tfl.vguardrishta.models.TransactionHistory
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 1/17/2019.
 */
class TransactionHistoryUseCase @Inject constructor(private val dataRepository: DataRepository) : FilterObjectCase<Unit,  Single<List<TransactionHistory>>>(){

    override fun execute(filterObject: FilterObject): Single<List<TransactionHistory>> = dataRepository.getTransactionHistory(filterObject)

}
package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.RedemptionHistory
import io.reactivex.Single
import javax.inject.Inject


class GetRedeemHistoryUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<String, Single<List<RedemptionHistory>>>() {

    override fun execute(parameters: String): Single<List<RedemptionHistory>> = dataRepository.getRedemptionHistory(parameters)

}
package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.PointsSummary
import io.reactivex.Single
import javax.inject.Inject


class GetMonthWiseEarningUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<Pair<String, String>, Single<PointsSummary>>() {

    override fun execute(parameters: Pair<String, String>): Single<PointsSummary> =
        dataRepository.getMonthWiseEarning(parameters)
}
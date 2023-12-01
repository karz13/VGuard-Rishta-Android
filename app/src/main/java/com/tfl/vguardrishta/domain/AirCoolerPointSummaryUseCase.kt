package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.PointsSummary
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 4/5/2019.
 */
class AirCoolerPointSummaryUseCase @Inject constructor(private val dataRepository: DataRepository) : ListUseCase<Unit, Single<PointsSummary>>() {
    override fun execute() = dataRepository.getAirCoolerPointsSummary()
    fun getAirCoolerSchemeDetails() = dataRepository.getAirCoolerSchemeDetails()

}
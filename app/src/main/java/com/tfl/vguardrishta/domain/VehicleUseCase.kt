package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class VehicleUseCase @Inject constructor(private val dataRepository: DataRepository) :
    ListUseCase<Unit, Single<List<String>>>() {
    override fun execute(): Single<List<String>> = dataRepository.getVehicleSegment()
}

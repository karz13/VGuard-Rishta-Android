package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.CouponResponse
import io.reactivex.Single
import javax.inject.Inject

class GetScanCodeHistoryUseCase @Inject constructor(private val dataRepository: DataRepository) :
    ListUseCase<Unit, Single<List<CouponResponse>>>() {

    override fun execute(): Single<List<CouponResponse>> = dataRepository.getScanCodeHistory()
    fun getAirCoolerScanCodeHistory(): Single<List<CouponResponse>> = dataRepository.getAirCoolerScanCodeHistory()

}
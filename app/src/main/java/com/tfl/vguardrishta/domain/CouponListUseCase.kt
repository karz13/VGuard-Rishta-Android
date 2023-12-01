package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.RedemptionCouponMaster
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 6/4/2019.
 */
class CouponListUseCase  @Inject constructor(private val dataRepository: DataRepository) :
    ListUseCase<Unit, Single<List<RedemptionCouponMaster>>>() {

    override fun execute(): Single<List<RedemptionCouponMaster>> = dataRepository.getCouponList()

}
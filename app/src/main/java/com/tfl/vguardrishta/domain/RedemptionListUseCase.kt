package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.FilterObject
import com.tfl.vguardrishta.models.RedemptionOrder
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 1/17/2019.
 */
class RedemptionListUseCase @Inject constructor(private val dataRepository: DataRepository) :
    FilterObjectCase<Unit, Single<List<RedemptionOrder>>>() {

    override fun execute(filterObject: FilterObject): Single<List<RedemptionOrder>> = dataRepository.getRedemptionList(filterObject)

}
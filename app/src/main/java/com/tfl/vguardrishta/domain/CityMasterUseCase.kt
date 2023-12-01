package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.CityMaster
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 4/16/2019.
 */
class CityMasterUseCase @Inject constructor(private val dataRepository: DataRepository) :
    TransactionCase<Unit, Single<List<CityMaster>>>() {

    override fun execute(id : Long): Single<List<CityMaster>> = dataRepository.getCities(id)

}
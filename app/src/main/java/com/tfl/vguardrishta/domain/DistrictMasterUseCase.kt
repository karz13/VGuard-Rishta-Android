package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.DistrictMaster
import com.tfl.vguardrishta.remote.RemoteDataSource
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 4/16/2019.
 */
class DistrictMasterUseCase @Inject constructor(
    private val dataRepository: DataRepository,
    private val remoteDataSource: RemoteDataSource
) :
    TransactionCase<Unit, Single<List<DistrictMaster>>>() {

    override fun execute(id: Long): Single<List<DistrictMaster>> = dataRepository.getDistricts(id)

    fun getDistrictMaster(id: Long): Single<List<DistrictMaster>> =
        dataRepository.getDistrictMaster(id)

    fun getDistrictsFromCrmApi(stateName: String): Single<List<DistrictMaster>> =
        remoteDataSource.getDistrictsFromCrmApi(stateName)


}
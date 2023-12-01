package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.CityMaster
import com.tfl.vguardrishta.models.PincodeDetails
import com.tfl.vguardrishta.models.StateMaster
import com.tfl.vguardrishta.remote.RemoteDataSource
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 4/16/2019.
 */
class StateMasterUseCase @Inject constructor(
    private val dataRepository: DataRepository,
    private val remoteDataSource: RemoteDataSource
) :
    ListUseCase<Unit, Single<List<StateMaster>>>() {

    override fun execute(): Single<List<StateMaster>> = dataRepository.getStates()

    fun getDetailsByPinCode(pinCode: String): Single<PincodeDetails> {
        return dataRepository.getDetailsByPinCode(pinCode)
    }

    fun getPincodeList(pinCode: String): Single<List<PincodeDetails>> {
        return dataRepository.getPincodeList(pinCode)
    }

    fun getStateDistCitiesByPincodeId(pinCodeId: String): Single<List<CityMaster>> {
        return dataRepository.getStateDistCitiesByPincodeId(pinCodeId)
    }

    fun getStatesFromCrmApi(): Single<List<StateMaster>> {
        return remoteDataSource.getStatesFromCrmApi()
    }

    fun getCRMStateDistrictByPincode(pinCode: String) = remoteDataSource.getCRMStateDistrictByPincode(pinCode)
    fun getCRMPinCodeList(pinCode: String) = remoteDataSource.getCRMPinCodeList(pinCode)

}
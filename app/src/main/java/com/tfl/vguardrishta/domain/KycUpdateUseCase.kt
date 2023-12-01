package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.KycDetails
import com.tfl.vguardrishta.models.KycRetailerDetails
import com.tfl.vguardrishta.models.Status
import io.reactivex.Single
import javax.inject.Inject

open class KycUpdateUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<KycDetails, Single<Status>>() {

    override fun execute(kycDetails: KycDetails): Single<Status> =
        dataRepository.updateKyc(kycDetails)

    fun executeForRetailer(kycDetails: KycRetailerDetails): Single<Status> =
        dataRepository.updateKycRetailer(kycDetails)

}
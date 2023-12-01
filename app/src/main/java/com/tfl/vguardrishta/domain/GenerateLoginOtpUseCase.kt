package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.models.VguardRishtaUser
import io.reactivex.Single
import javax.inject.Inject


class GenerateLoginOtpUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<VguardRishtaUser, Single<Status>>() {
    override fun execute(parameters: VguardRishtaUser): Single<Status> =
        dataRepository.generateOtpForLogin(parameters)

    fun validateLoginOtp(vguardRishtaUser: VguardRishtaUser): Single<Status> {
        return dataRepository.validateLoginOtp(vguardRishtaUser)
    }


}
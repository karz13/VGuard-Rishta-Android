package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.models.VguardRishtaUser
import io.reactivex.Single
import javax.inject.Inject

open class ValidateNewMobileUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<VguardRishtaUser, Single<Status>>() {

    override fun execute(vru: VguardRishtaUser): Single<Status> =
        dataRepository.validateNewMobileNo(vru)

    fun validateOtp(vguardRishtaUser: VguardRishtaUser): Single<Status> {
        return dataRepository.validateOtp(vguardRishtaUser)
    }

}
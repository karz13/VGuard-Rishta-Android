package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.models.VguardRishtaUser
import io.reactivex.Single
import javax.inject.Inject

open class NewUserRegisterUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<VguardRishtaUser, Single<Status>>() {

    override fun execute(vguardRishtaUser: VguardRishtaUser): Single<Status> =
        dataRepository.registerNewUser(vguardRishtaUser)

    fun getReferralName(code: String): Single<VguardRishtaUser> {
        return dataRepository.getReferralName(code)
    }

}
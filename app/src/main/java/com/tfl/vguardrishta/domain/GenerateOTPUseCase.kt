package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.models.User
import com.tfl.vguardrishta.models.VguardRishtaUser
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 4/24/2019.
 */
class GenerateOTPUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UserUploadCase<Unit, Single<Status>>() {

    override fun execute(user: User): Single<Status> = dataRepository.generateOtp(user)
    fun forgotPassword(user: VguardRishtaUser): Single<Status> {

        return dataRepository.forgotPassword(user)
    }


}
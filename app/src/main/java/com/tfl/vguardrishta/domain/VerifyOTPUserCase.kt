package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.User
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 4/24/2019.
 */
class VerifyOTPUserCase @Inject constructor(private val dataRepository: DataRepository) :
    UserUploadCase<Unit, Single<User>>() {
    override fun execute(user: User): Single<User> = dataRepository.verifyOTP(user)

}
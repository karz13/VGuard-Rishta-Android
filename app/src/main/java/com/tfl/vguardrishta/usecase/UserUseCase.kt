package com.tfl.vguardrishta.usecase

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.domain.UseCase
import com.tfl.vguardrishta.models.SPCoupon
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.models.User
import io.reactivex.Single
import javax.inject.Inject

class UserUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<String, Single<SPCoupon>>() {

    override fun execute(parameters: String): Single<SPCoupon> =
        dataRepository.getUserByMobile(parameters)

    fun updateUser(user: User): Single<Status> = dataRepository.updateUser(user)

    fun updateToken(token: String): Single<Status> = dataRepository.updateToken(token)

    fun getDetails(): Single<User?> = dataRepository.getDetails()

    fun submitDetails(target: Int): Single<Status?> = dataRepository.submitDetails(target)

    fun updateLogoutStatus(): Single<Status> = dataRepository.updateLogoutStatus()
}
package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.models.User
import com.tfl.vguardrishta.models.VguardRishtaUser
import io.reactivex.Single
import javax.inject.Inject

open class AuthenticateUserCaseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<String, Single<User>>() {

    override fun execute(parameters: String): Single<User> =
        dataRepository.authenticateUser(parameters)

    fun getUser(): Single<VguardRishtaUser> {
        return  dataRepository.getUser()
    }

    fun userLoginDetails(): Single<VguardRishtaUser> {
        return  dataRepository.userLoginDetails()
    }

    fun logoutUser(): Single<Status> {
        return dataRepository.logoutUser()
    }

    fun setSelectedLangId(vguardRishtaUser: VguardRishtaUser): Single<Status> {
        return dataRepository.setSelectedLangId(vguardRishtaUser)
    }

}
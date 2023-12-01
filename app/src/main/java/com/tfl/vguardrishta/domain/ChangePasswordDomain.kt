package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.ChangePassword
import com.tfl.vguardrishta.models.Status
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 6/13/2019.
 */
class ChangePasswordDomain @Inject constructor(private val dataRepository: DataRepository) :
    ChangePasswordCase<Unit, Single<Status>>() {

    override fun execute(changePassword: ChangePassword): Single<Status> = dataRepository.changePassword(changePassword)

}
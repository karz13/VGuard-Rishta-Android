package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.models.User
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 4/5/2019.
 */
class UploadUserUseCase @Inject constructor(private val dataRepository: DataRepository) : UserUploadCase<Unit, Single<Status>>(){

    override fun execute(user: User): Single<Status> = dataRepository.sendUser(user)

}
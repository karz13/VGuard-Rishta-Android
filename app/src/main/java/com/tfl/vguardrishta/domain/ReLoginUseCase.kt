package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.User
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 1/21/2019.
 */
class ReLoginUseCase @Inject constructor(private val dataRepository: DataRepository) : UseCase<Unit, Single<User>>(){
    override fun execute(parameters: Unit): Single<User>  =dataRepository.reLogin()
}
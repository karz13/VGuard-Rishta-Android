package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.User
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 6/14/2019.
 */
 class GetDistributorListUseCase @Inject constructor(private val dataRepository: DataRepository) :
    ListUseCase<Unit, Single<List<User>>>(){

    override fun execute(): Single<List<User>> = dataRepository.getDistList()

}
package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.FilterObject
import com.tfl.vguardrishta.models.User
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 3/26/2019.
 */
class UserListingUseCase @Inject constructor(private val dataRepository: DataRepository) :
    FilterObjectCase<Unit, Single<List<User>>>() {

    override fun execute(filterObject: FilterObject): Single<List<User>> = dataRepository.getUsers(filterObject)

    fun getTerritoryManager(filterObject: FilterObject): Single<List<User>> = dataRepository.getTerritoryManager(filterObject)

}
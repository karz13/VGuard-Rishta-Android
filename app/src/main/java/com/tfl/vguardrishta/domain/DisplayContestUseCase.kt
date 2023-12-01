package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.DisplayContest
import com.tfl.vguardrishta.models.Status
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Joshi on 16-12-2019.
 */
class DisplayContestUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<DisplayContest, Single<Status>>() {

    override fun execute(parameters: DisplayContest): Single<Status> =
        dataRepository.createDisplayContest(parameters)

    fun execute(apmId: Long, mobileNo: String): Single<DisplayContest> =
        dataRepository.loadContestDetails(apmId, mobileNo)
}
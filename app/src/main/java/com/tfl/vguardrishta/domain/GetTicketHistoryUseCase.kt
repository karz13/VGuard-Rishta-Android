package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.TicketType
import io.reactivex.Single
import javax.inject.Inject


class GetTicketHistoryUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<Unit, Single<List<TicketType>>>() {

    override fun execute(parameters: Unit): Single<List<TicketType>> = dataRepository.getTicketHistory()

}
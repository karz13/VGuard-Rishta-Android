package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.RaiseTicket
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.models.TicketType
import io.reactivex.Single
import javax.inject.Inject


class SendTicketUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<RaiseTicket, Single<Status>>() {

    override fun execute(rt: RaiseTicket): Single<Status> = dataRepository.sendTicket(rt)

}
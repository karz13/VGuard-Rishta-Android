package com.tfl.vguardrishta.domain



import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.ErrorCoupon
import com.tfl.vguardrishta.models.RaiseTicket
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.models.TicketType
import io.reactivex.Single
import javax.inject.Inject


class ProcessErrorCouponUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<ErrorCoupon, Single<Status>>() {

    override fun execute(ec: ErrorCoupon): Single<Status> = dataRepository.processErrorCoupon(ec)

}
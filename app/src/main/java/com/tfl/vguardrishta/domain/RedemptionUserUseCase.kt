package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.Redemption
import com.tfl.vguardrishta.models.RedemptionOrder
import com.tfl.vguardrishta.models.Status
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 4/25/2019.
 */
class RedemptionUserUseCase @Inject constructor(private val dataRepository: DataRepository) :
    RedemptionUseCase<Unit, Single<Status>>() {

    override fun execute(redemption: Redemption): Single<Status> = dataRepository.redeem(redemption)

    fun fetchPendingApproval(screen: String): Single<List<RedemptionOrder>> =
        dataRepository.fetchPendingApproval(screen)

    fun updatePendingApproval(redemptionOrder: RedemptionOrder): Single<Status> =
        dataRepository.updatePendingApproval(redemptionOrder)
}
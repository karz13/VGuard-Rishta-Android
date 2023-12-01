package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.models.Redemption

/**
 * Created by Shanmuka on 4/25/2019.
 */
abstract class RedemptionUseCase<in P, R> {

    @Throws(RuntimeException::class)
    abstract fun execute(redemption: Redemption): R

}
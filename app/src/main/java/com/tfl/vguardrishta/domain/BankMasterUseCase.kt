package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.models.BankMaster
import io.reactivex.Single

abstract class BankMasterUseCase<in P, R> {
    @Throws(RuntimeException::class)
    abstract fun execute(ifscCode: String): Single<BankMaster?>
}
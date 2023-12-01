package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.BankMaster
import io.reactivex.Single
import javax.inject.Inject

class BankDetailsUseCase @Inject constructor(private val dataRepository: DataRepository) :
    BankMasterUseCase<Unit, Single<BankMaster>>() {
    override fun execute(ifscCode: String): Single<BankMaster?> = dataRepository.getByIfsc(ifscCode)
}
package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.ProductOrder
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.models.BankDetail
import io.reactivex.Single
import javax.inject.Inject

open class BankTransferUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<ProductOrder, Single<Status>>() {

    override fun execute(po: ProductOrder): Single<Status> =
        dataRepository.bankTransfer(po)

    fun getBankDetail(): Single<BankDetail> {
        return dataRepository.getBankDetail()
    }

    fun getBanks(): Single<List<BankDetail>> {
        return  dataRepository.getBanks()
    }



}
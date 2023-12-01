package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.InvoiceDetails
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Joshi on 24-09-2019.
 */
class InvoiceListUseCase @Inject constructor(private val dataRepository: DataRepository) :
    ListUseCase<Unit, Single<List<InvoiceDetails>>>() {

    override fun execute(): Single<List<InvoiceDetails>> = dataRepository.getInvoiceList()

}
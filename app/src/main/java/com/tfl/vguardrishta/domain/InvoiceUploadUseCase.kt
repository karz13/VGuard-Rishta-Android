package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.Invoice
import com.tfl.vguardrishta.models.Status
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Joshi on 24-09-2019.
 */
class InvoiceUploadUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<Invoice, Single<Status>>() {
    override fun execute(parameters: Invoice): Single<Status> = dataRepository.uploadInvoice(parameters)
}
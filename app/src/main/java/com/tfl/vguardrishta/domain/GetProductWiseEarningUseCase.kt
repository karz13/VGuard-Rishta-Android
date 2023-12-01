package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.ProductWiseEarning
import io.reactivex.Single
import javax.inject.Inject

class GetProductWiseEarningUseCase @Inject constructor(private val dataRepository: DataRepository) :
    ListUseCase<Unit, Single<List<ProductWiseEarning>>>() {

    override fun execute(): Single<List<ProductWiseEarning>> = dataRepository.getProdWiseEarning()

}
package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.*
import io.reactivex.Single
import javax.inject.Inject

class GetSchemeWiseEarningUseCase @Inject constructor(private val dataRepository: DataRepository) :
    ListUseCase<Unit, Single<List<SchemeWiseEarning>>>() {

    override fun execute(): Single<List<SchemeWiseEarning>> = dataRepository.getSchemeWiseEarning()

}
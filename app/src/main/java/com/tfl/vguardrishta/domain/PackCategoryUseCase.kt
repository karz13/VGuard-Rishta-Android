package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.PackCategory
import io.reactivex.Single
import javax.inject.Inject

class PackCategoryUseCase @Inject constructor(private val dataRepository: DataRepository) :
    ListUseCase<Unit, Single<List<PackCategory>>>() {

    override fun execute(): Single<List<PackCategory>> = dataRepository.getPackCategoryList()
}
package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.PackProduct
import io.reactivex.Single
import javax.inject.Inject

class SelectedSegmentUseCase @Inject constructor(private val dataRepository: DataRepository) :
    SelectSegmentUseCase<Unit, Single<List<PackProduct>>>() {
    override fun execute(segment: String, category: Long): Single<List<PackProduct>> =
        dataRepository.getSelectedProductList(segment, category)

}

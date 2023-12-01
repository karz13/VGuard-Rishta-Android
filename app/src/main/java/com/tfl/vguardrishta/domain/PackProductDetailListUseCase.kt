package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.PackProduct
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 3/22/2019.
 */
class PackProductDetailListUseCase @Inject constructor(private val dataRepository: DataRepository) :
PackProductsUseCase<Unit, Single<List<PackProduct>>>() {

    override fun execute(categoryId: Long): Single<List<PackProduct>> = dataRepository.getPackProductListing(categoryId)

}
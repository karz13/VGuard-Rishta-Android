package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.ProductCategory
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 3/22/2019.
 */
class ListListUseCase @Inject constructor(private val dataRepository: DataRepository) :
    ListUseCase<Unit, Single<List<ProductCategory>>>() {

    override fun execute(): Single<List<ProductCategory>> = dataRepository.getProductCategoryList()

}
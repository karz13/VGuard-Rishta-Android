package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.Category
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 5/9/2019.
 */
class CategoryListUseCase @Inject constructor(private val dataRepository: DataRepository) :
    ListUseCase<Unit, Single<List<Category>>>() {

    override fun execute(): Single<List<Category>> = dataRepository.getCategoryList()

}
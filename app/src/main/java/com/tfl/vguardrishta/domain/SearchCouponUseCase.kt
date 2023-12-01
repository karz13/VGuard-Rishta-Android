package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.SearchCoupon
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 5/9/2019.
 */
class SearchCouponUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<String, Single<SearchCoupon>>() {
    override fun execute(parameters: String): Single<SearchCoupon> = dataRepository.searchCoupon(parameters)
}
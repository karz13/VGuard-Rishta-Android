package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.models.TierPoints
import com.tfl.vguardrishta.remote.RemoteDataSource
import io.reactivex.Single
import javax.inject.Inject

class TierDetailUseCase @Inject constructor(private val remote: RemoteDataSource) :
    ListUseCase<Unit, Single<TierPoints>>() {

    override fun execute(): Single<TierPoints> = remote.getTierDetail()

}
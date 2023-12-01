package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 5/14/2019.
 */ class AppVersionUseCase @Inject constructor(private val dataRepository: DataRepository) :
    ListUseCase<Unit, Single<String>>() {

    override fun execute(): Single<String> = dataRepository.getAppVersion()

}
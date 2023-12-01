package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.SchemeImages
import io.reactivex.Single
import javax.inject.Inject


class GetInfoDeskBannersUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<Unit, Single<List<SchemeImages>>>() {

    override fun execute(parameters: Unit): Single<List<SchemeImages>> = dataRepository.getInfoDeskBanners()

}
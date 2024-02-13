package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.SpecialSchemes
import io.reactivex.Single
import javax.inject.Inject

class GetSpecialOffersUseCase @Inject constructor( private val dataRepository:DataRepository):UseCase<Unit, Single<List<SpecialSchemes>>>() {

    override fun execute(parameter:Unit): Single<List<SpecialSchemes>> {
        return dataRepository.getSpecialOffers()
    }


}
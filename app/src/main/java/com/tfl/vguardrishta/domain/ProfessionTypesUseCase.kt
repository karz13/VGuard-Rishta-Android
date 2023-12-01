package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.models.Profession
import com.tfl.vguardrishta.data.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class ProfessionTypesUseCase @Inject constructor(private val dataRepository: DataRepository) :
    ListUseCase<Unit, Single<List<Profession>>>() {


    fun getSubProfessions(professionId: String): Single<List<Profession>> {
        return dataRepository.getSubProfessions(professionId)
    }

    fun getProfessions(isService: Int): Single<List<Profession>>  = dataRepository.getProfession(isService)
    override fun execute(): Single<List<Profession>> {
        TODO("Not yet implemented")
    }
}
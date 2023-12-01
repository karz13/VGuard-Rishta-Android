package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.KycDetails
import com.tfl.vguardrishta.models.KycIdTypes
import io.reactivex.Single
import javax.inject.Inject

class GetKycIdTypesUseCase @Inject constructor(private val dataRepository: DataRepository) :
    ListUseCase<Unit, Single<List<KycIdTypes>>>() {

    override fun execute(): Single<List<KycIdTypes>> = dataRepository.getKycIdTypes()

     fun getKycIdTypesByLang(selectedLangId:Int): Single<List<KycIdTypes>> = dataRepository.getKycIdTypes()
    fun getKycDetails(): Single<KycDetails> {
        return dataRepository.getKycDetails()
    }

}
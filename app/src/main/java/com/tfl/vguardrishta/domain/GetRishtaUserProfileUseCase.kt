package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.BankDetail
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.models.VguardRishtaUser
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Shanmuka on 4/16/2019.
 */
class GetRishtaUserProfileUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<Unit, Single<VguardRishtaUser>>() {

    override fun execute(unit:Unit): Single<VguardRishtaUser> = dataRepository.getRishtaUserProfile()

    fun updateProfile(vru: VguardRishtaUser): Single<Status> {
        return  dataRepository.updateProfile(vru)
    }

    fun updateBank(bankDetail: BankDetail): Single<Status> {
        return  dataRepository.updateBank(bankDetail)
    }

    fun reUpdateUserForKyc(vru: VguardRishtaUser): Single<Status> {
        return  dataRepository.reUpdateUserForKyc(vru)
    }

}
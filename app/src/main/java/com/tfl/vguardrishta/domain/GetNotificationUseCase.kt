package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.models.Notifications
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.models.VguardRishtaUser
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import javax.inject.Inject


class GetNotificationUseCase @Inject constructor(private val dataRepository: DataRepository) :
    UseCase<Unit, Single<List<Notifications>>>() {

    override fun execute(parameters: Unit): Single<List<Notifications>> =
        dataRepository.getNotifications()

    fun getNotificationCount(): Single<Notifications> {

        return dataRepository.getNotificationCount()
    }

    fun updateReadCheck(it: Notifications): Single<Status> {
        return dataRepository.updateReadCheck(it)
    }

    fun updateFcmToken(vguardRishtaUser: VguardRishtaUser): Single<Status> {
        return dataRepository.updateFcmToken(vguardRishtaUser)
    }

    fun getPushNotifications(): Single<List<Notifications>> {
        return dataRepository.getPushNotifications()
    }

}
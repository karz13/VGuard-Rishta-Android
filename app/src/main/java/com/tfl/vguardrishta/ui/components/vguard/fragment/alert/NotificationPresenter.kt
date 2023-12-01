package com.tfl.vguardrishta.ui.components.vguard.fragment.alert

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetNotificationUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.Notifications
import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject

class NotificationPresenter @Inject constructor(
    val context: Context,
    val getNotificationUseCase: GetNotificationUseCase

) : BasePresenter<NotificationContract.View>(), NotificationContract.Presenter {


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }


    override fun getNotifications() {
        disposables?.add(
            getNotificationUseCase.execute(Unit).applySchedulers()
                .doOnSubscribe { getView()?.showProgress() }.doFinally { getView()?.stopProgress() }
                .subscribe({
                    if (it != null && !it.isNullOrEmpty()) {
                        getView()?.showNotifications(it)
                    } else {
                        getView()?.showNoData()
                    }
                },
                    {
                        getView()?.showToast(context.getString(R.string.something_wrong))
                    })
        )
    }

    override fun getNotificationCount() {
        disposables?.add(getNotificationUseCase.getNotificationCount().applySchedulers()
            .doOnSubscribe { }
            .doFinally { }
            .subscribe({
                if (it?.count != null) {
                    getView()?.showNotificationCount(it.count)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )

    }

    override fun sendNotificationRead(it: Notifications) {
        disposables?.add(getNotificationUseCase.updateReadCheck(it).applySchedulers()
            .doOnSubscribe { }
            .doFinally { }
            .subscribe({
                getNotificationCount()
                if (it?.code == 200) {
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }
}
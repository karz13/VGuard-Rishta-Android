package com.tfl.vguardrishta.ui.components.vguard.infodesk

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetInfoDeskBannersUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject

class InfoDeskPresenter @Inject constructor(
    val context: Context,
    val getInfoDeskBannersUseCase: GetInfoDeskBannersUseCase

) : BasePresenter<InfoDeskContract.View>(), InfoDeskContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getInfoDeskBanners() {
        disposables?.add(getInfoDeskBannersUseCase.execute(Unit).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                getView()?.showInfoDeskBanner(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }
}
package com.tfl.vguardrishta.ui.components.vguard.tierDetail

import android.content.Context
import com.tfl.vguardrishta.domain.TierDetailUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject

class TierDetailPresenter @Inject constructor(
    val context: Context, val tierDetailUseCase: TierDetailUseCase
) : BasePresenter<TierDetailContract.View>(), TierDetailContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getTierDetails() {
        disposables?.add(tierDetailUseCase.execute().applySchedulers()
            .doOnSubscribe { getView()?.showProgress()}
            .doFinally {getView()?.stopProgress()}.subscribe({
                getView()?.showTierDetail(it)
            }, {
                getView()?.showError()
            }))
    }
}
package com.tfl.vguardrishta.ui.components.vguard.schemes

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetSchemeBannersUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject

class SchemeAndOffersPresenter @Inject constructor(
    val getSchemeBannersUseCase: GetSchemeBannersUseCase,
    val context: Context
) : BasePresenter<SchemeAndOffersContract.View>(), SchemeAndOffersContract.Presenter {


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getSchemesBanners() {
        disposables?.add(getSchemeBannersUseCase.execute(Unit).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                getView()?.showSchemes(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }
}
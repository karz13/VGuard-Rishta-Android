package com.tfl.vguardrishta.ui.components.vguard.specialComboOffers

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetSpecialOffersUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.SpecialSchemes
import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject

class SpecialComboOffersPresenter @Inject constructor(
    val context: Context, val getSpecialOffersUseCase: GetSpecialOffersUseCase
):BasePresenter<SpecialComboOffersContract.View>(),SpecialComboOffersContract.Presenter{
    override fun getSpecialOffers() {
        disposables?.add(getSpecialOffersUseCase.execute(Unit).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                getView()?.loadCards(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }


}
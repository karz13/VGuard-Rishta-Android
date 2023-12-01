package com.tfl.vguardrishta.ui.components.vguard.welfare

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetWhatsNewUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject

class WelfarePresenter @Inject constructor(
    val whatsNewUseCase: GetWhatsNewUseCase,
    val context: Context
) : BasePresenter<WelfareContract.View>(), WelfareContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getWelfare() {
        disposables?.add(whatsNewUseCase.getWelfarePdfList().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if(!it.isNullOrEmpty()){
                    getView()?.showWelfare(it)
                }else{
                    getView()?.showNoData()
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }
}
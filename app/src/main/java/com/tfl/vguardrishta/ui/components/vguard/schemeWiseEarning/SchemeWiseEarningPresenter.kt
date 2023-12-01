package com.tfl.vguardrishta.ui.components.vguard.schemeWiseEarning

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetSchemeWiseEarningUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject

class SchemeWiseEarningPresenter @Inject constructor(
    private val context: Context,
    private val getSchemeWiseEarningUseCase: GetSchemeWiseEarningUseCase
) : BasePresenter<SchemeWiseEarningContract.View>(),
    SchemeWiseEarningContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getEarningDetails() {
        disposables?.add(
            getSchemeWiseEarningUseCase.execute().applySchedulers()
                .doOnSubscribe { getView()?.showProgress() }.doFinally { getView()?.stopProgress() }
                .subscribe({
                    if (it != null && !it.isNullOrEmpty()) {
                        getView()?.showEarningDetails(it)
                    } else {
                        getView()?.showNoData()
                    }
                },
                    {
                        getView()?.showToast(context.getString(R.string.something_wrong))
                    })
        )

    }

}
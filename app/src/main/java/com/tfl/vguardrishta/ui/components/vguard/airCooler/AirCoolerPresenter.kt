package com.tfl.vguardrishta.ui.components.vguard.airCooler

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.AirCoolerPointSummaryUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.PointsSummary
import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject

class AirCoolerPresenter @Inject constructor(
    val context: Context,
    private val getAirCoolerPointSummaryUseCase: AirCoolerPointSummaryUseCase
) : BasePresenter<AirCoolerContract.View>(), AirCoolerContract.Presenter {

    override fun getAirCoolerPointSummary() {
        disposables?.add(getAirCoolerPointSummaryUseCase.execute().applySchedulers()
            .doOnSubscribe { }
            .doFinally { }
            .subscribe({
                if (it != null) {
                    getAirCoolerPointSummary(it)
                }
            }, {

                getView()?.showToast(context.getString(R.string.something_wrong))

            })
        )
    }

    override fun getAirCoolerSchemeDetails() {
        disposables?.add(getAirCoolerPointSummaryUseCase.getAirCoolerSchemeDetails().applySchedulers()
            .doOnSubscribe { }
            .doFinally { }
            .subscribe({
                if (it != null) {
                   // val pdfUrl = it.strUrl
                   getView()?.showSchemeDetails(it.strUrl!!)
                }
            }, {

                getView()?.showToast(context.getString(R.string.something_wrong))

            })
        )
    }

    private fun getAirCoolerPointSummary(it: PointsSummary) {
        // Paper.book().write(Constants.KEY_RISHTA_USER, it)
        getView()?.showUserDetails(it)
        //CacheUtils.refreshView(false)
    }


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

}
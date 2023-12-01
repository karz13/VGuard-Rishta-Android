package com.tfl.vguardrishta.ui.components.vguard.tdsStatement

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetWhatsNewUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.MonthData
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.AppUtils
import javax.inject.Inject

class TDSstatementPresenter  @Inject constructor(
    val whatsNewUseCase: GetWhatsNewUseCase,
    val context: Context
) : BasePresenter<TDSstatementContract.View>(), TDSstatementContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }


    private fun processAy(it: List<String>?) {
        getView()?.setFiscalYearSpinner(it as ArrayList<String>)
    }

    override fun getFiscalYear() {
        if(!AppUtils.isNetworkAvailable(context)){
            getView()?.showToast(context.getString(R.string.no_internet))
            return
        }
        disposables?.add(whatsNewUseCase.getFiscalYear().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
               if(it!=null)
               {
                   processAy(it)
               }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    fun getMonth() {
        if(!AppUtils.isNetworkAvailable(context)){
            getView()?.showToast(context.getString(R.string.no_internet))
            return
        }
        disposables?.add(whatsNewUseCase.getMonth().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if(it!=null)
                {
                    getView()?.setMonthSP(it)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

   override fun getTDSStatement(id: String, year: String) {
       var month=MonthData()
       month.id=id
       month.year=year
        disposables?.add(whatsNewUseCase.getTdsStatementList(month).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if(!it.isNullOrEmpty()){
                    getView()?.showData(it)
                }else{
                    getView()?.showNoData()
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }
}
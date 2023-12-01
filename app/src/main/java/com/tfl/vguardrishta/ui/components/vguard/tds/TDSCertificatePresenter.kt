package com.tfl.vguardrishta.ui.components.vguard.tds

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetWhatsNewUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.ui.components.vguard.welfare.WelfareContract
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import javax.inject.Inject

class TDSCertificatePresenter  @Inject constructor(
    val whatsNewUseCase: GetWhatsNewUseCase,
    val context: Context
) : BasePresenter<TDSCertificateContract.View>(), TDSCertificateContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getTDSCertificate(accessmentYear : String) {
        disposables?.add(whatsNewUseCase.getTdsList(accessmentYear).applySchedulers()
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

    override fun getAccessmentYear() {
        if(!AppUtils.isNetworkAvailable(context)){
            getView()?.showToast(context.getString(R.string.no_internet))
            return
        }
        disposables?.add(whatsNewUseCase.getAy().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processAy(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun processAy(it: List<String>?) {
        val arrayList = CacheUtils.getFirstAccessmentYear(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        getView()?.setAccementSpinner(arrayList)
    }
}
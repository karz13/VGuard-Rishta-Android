package com.tfl.vguardrishta.ui.components.vguard.paytm

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetPaytmProductIdUseCase
import com.tfl.vguardrishta.domain.PaytmTransferUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.ProductOrder
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import javax.inject.Inject

class PaytmTransferPresenter @Inject constructor(
    val context: Context,
    val paytmTransferUseCase: PaytmTransferUseCase,
    val getPaytmProductIdUseCase: GetPaytmProductIdUseCase
) : BasePresenter<PaytmTransferContract.View>(), PaytmTransferContract.Presenter {


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun validateAndTransfer(points: String, mobile: String,screenType : String?) {
        if (mobile.isEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_paytm_mobile_no))
            return
        }

        if (!AppUtils.isValidMobileNo(mobile)) {
            getView()?.showToast(context.getString(R.string.enter_valid_mobileNo))
            return
        }


        if (points.isEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_points_toredeem))
            return
        }

        val productOrder = ProductOrder()
        productOrder.points=points
        productOrder.mobileNo=mobile

        if(screenType!=Constants.AIRCOOLER_SCREEN)
        {
        disposables?.add(paytmTransferUseCase.execute(productOrder).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processResult(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
        }
        else {
            disposables?.add(paytmTransferUseCase.executeForAirCooler(productOrder).applySchedulers()
                .doOnSubscribe { getView()?.showProgress() }
                .doFinally { getView()?.stopProgress() }
                .subscribe({
                    processResult(it)
                }, {
                    getView()?.showToast(context.getString(R.string.something_wrong))
                })
            )
        }

    }

    override fun getPaytmProductId() {
        disposables?.add(getPaytmProductIdUseCase.execute(Unit).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    getView()?.setPaytmProductId(it)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun processResult(it: Status?) {
        if (it == null) {
            getView()?.showToast(context.getString(R.string.problem_occurred))
            return
        }
        if (it.code == 200) {
            getView()?.showMsgDialogWithFinish(it.message!!)
            CacheUtils.refreshView(true)
        } else if (it.code == 400) {
            getView()?.showMsgDialog(it.message!!)
        }
    }
}
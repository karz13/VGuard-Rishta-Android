package com.tfl.vguardrishta.ui.components.vguard.referralCode

import android.content.Context
import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject

class ReferralCodePresenter @Inject constructor(
    private val context: Context) : BasePresenter<ReferralCodeContract.View>(),
    ReferralCodeContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }


}
package com.tfl.vguardrishta.ui.components.vguard.engagement

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetWhatsNewUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import javax.inject.Inject

class EngagementPresenter  @Inject constructor(
    val whatsNewUseCase: GetWhatsNewUseCase,
    val context: Context
) : BasePresenter<EngagementContract.View>(), EngagementContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

}
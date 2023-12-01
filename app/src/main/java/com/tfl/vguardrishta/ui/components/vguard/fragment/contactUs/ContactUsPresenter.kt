package com.tfl.vguardrishta.ui.components.vguard.fragment.contactUs

import android.content.Context
import com.tfl.vguardrishta.domain.GetNotificationUseCase
import com.tfl.vguardrishta.ui.base.BasePresenter
import javax.inject.Inject

class ContactUsPresenter @Inject constructor(
    val context: Context,
    val getNotificationUseCase: GetNotificationUseCase

) : BasePresenter<ContactUsContract.View>(), ContactUsContract.Presenter {


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }
}
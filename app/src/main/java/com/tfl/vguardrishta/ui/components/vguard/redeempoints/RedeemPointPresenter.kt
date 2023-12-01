package com.tfl.vguardrishta.ui.components.vguard.redeempoints

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.AuthenticateUserCaseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.models.VguardRishtaUser
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import io.paperdb.Paper
import javax.inject.Inject

class RedeemPointPresenter @Inject constructor(
    val context: Context,
    val authenticateUserCaseCase: AuthenticateUserCaseCase
) : BasePresenter<RedeemPointContract.View>(), RedeemPointContract.Presenter {

    override fun getUserDetails() {
        disposables?.add(authenticateUserCaseCase.getUser().applySchedulers()
            .doOnSubscribe {  }
            .doFinally { }
            .subscribe({
                if (it != null) {
                    saveUserShowPoints(it)
                }
            }, {

                getView()?.showToast(context.getString(R.string.something_wrong))

            })
        )
    }

    private fun saveUserShowPoints(it: VguardRishtaUser) {
            Paper.book().write(Constants.KEY_RISHTA_USER, it)
            getView()?.showUserDetails()
            CacheUtils.refreshView(false)
    }


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

}
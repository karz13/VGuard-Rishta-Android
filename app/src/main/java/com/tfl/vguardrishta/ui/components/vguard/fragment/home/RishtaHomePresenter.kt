package com.tfl.vguardrishta.ui.components.vguard.fragment.home

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.AuthenticateUserCaseCase
import com.tfl.vguardrishta.domain.GetNotificationUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.VguardRishtaUser
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import io.paperdb.Paper
import javax.inject.Inject

class RishtaHomePresenter @Inject constructor(
    val context: Context,
    val authenticateUserCaseCase: AuthenticateUserCaseCase,
    val getNotificationUseCase: GetNotificationUseCase

) : BasePresenter<RishtaHomeContract.View>(), RishtaHomeContract.Presenter {


    private var isGetProfile: Boolean = false

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getUserDetails() {
        disposables?.add(authenticateUserCaseCase.getUser().applySchedulers()
            .doOnSubscribe { }
            .doFinally { }
            .subscribe({
                if (it.active != "1" && it.roleId == Constants.RET_USER_TYPE) {
                    getView()?.autoLogoutUser()
                } else if (it != null) {
                    saveUser(it, false)
                    isGetProfile = false
                }
            }, {
                isGetProfile = false
                if (it.message!!.contains("401")) {
                    getView()?.showToast(context.getString(R.string.invalidCredentials))
                } else {
                    getView()?.showToast(context.getString(R.string.something_wrong))
                }
            })
        )
    }

    private fun saveUser(user: VguardRishtaUser?, remember: Boolean) {
        Paper.book().write(Constants.KEY_RISHTA_USER, user)
        if (user?.appVersionCode != null) {
            CacheUtils.setDbAppVersion(user?.appVersionCode!!.toInt())
        }
        getView()?.showUserDetails()
        CacheUtils.refreshView(false)
    }

    override fun getNotificationCount() {
        disposables?.add(getNotificationUseCase.getNotificationCount().applySchedulers()
            .doOnSubscribe { }
            .doFinally { }
            .subscribe({
                if (it?.count != null) {
                    getView()?.showNotificationCount(it.count)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )

    }


}
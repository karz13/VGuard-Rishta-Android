package com.tfl.vguardrishta.ui.components.vguard.home

import android.content.Context
import android.util.Log
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

class HomePresenter @Inject constructor(
    val authenticateUserCaseCase: AuthenticateUserCaseCase,
    val context: Context,
    val getNotificationUseCase: GetNotificationUseCase
) : BasePresenter<HomeContract.View>(), HomeContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }


    override fun getUserDetails() {
        disposables?.add(authenticateUserCaseCase.getUser().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    saveUser(it, false)
                }
            }, {
                if (it.message!!.contains("401")) {
                    getView()?.showToast(context.getString(R.string.invalidCredentials))
                } else {
                    getView()?.showToast(context.getString(R.string.something_wrong))
                }
            })
        )
    }

    override fun logoutUser() {
        disposables?.add(authenticateUserCaseCase.logoutUser().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                getView()?.logoutUser()
            }, {
                if (it.message!!.contains("401")) {
//                    getView()?.showToast(context.getString(R.string.invalidCredentials))
                    getView()?.logoutUser()
                } else {
                    getView()?.showToast(context.getString(R.string.something_wrong))
                    getView()?.logoutUser()

                }
            })
        )
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

    override fun updateFcmToken(vguardRishtaUser: VguardRishtaUser) {
        disposables?.add(getNotificationUseCase.updateFcmToken(vguardRishtaUser).applySchedulers()
            .doOnSubscribe { }.doFinally { }.subscribe({
                Log.d("tokenUpdate", "Success")
            }, {
                Log.d("tokenUpdate", "Failed")
            }))
    }

    override fun sendSelectedLang(selectedLangId: Int) {
        val vguardRishtaUser = VguardRishtaUser()
        vguardRishtaUser.preferredLanguagePos = selectedLangId.toString()

        disposables?.add(authenticateUserCaseCase.setSelectedLangId(vguardRishtaUser)
            .applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null && it.code == 200) {
                    CacheUtils.setSendSelectedLang(false)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun saveUser(user: VguardRishtaUser?, remember: Boolean) {
        Paper.book().write(Constants.KEY_RISHTA_USER, user)
        CacheUtils.writeIsUserRefreshReq(false)
    }

    fun getPushNotifications() {
        disposables?.add(getNotificationUseCase.getPushNotifications().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }.subscribe({
                if (it.isNotEmpty()) {
                    getView()?.showNotificationDialog(it)
                }
            }, {
                getView()?.showError()
            }))
    }

}
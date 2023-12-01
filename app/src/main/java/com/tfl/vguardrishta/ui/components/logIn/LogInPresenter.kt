package com.tfl.vguardrishta.ui.components.logIn

import android.content.Context
import android.util.Base64
import com.tfl.vguardrishta.BuildConfig
//import com.tfl.minda.BuildConfig
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.AppVersionUseCase
import com.tfl.vguardrishta.domain.AuthenticateUserCaseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.Creds
import com.tfl.vguardrishta.models.User
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Constants.BRANCH_MANAGER
import com.tfl.vguardrishta.utils.Constants.CREDS
import com.tfl.vguardrishta.utils.Constants.HEAD_MANAGER
import com.tfl.vguardrishta.utils.Constants.KEY_USER
import com.tfl.vguardrishta.utils.Constants.REGIONAL_MANAGER
import com.tfl.vguardrishta.utils.Constants.TERRITORY_MANAGER
import com.tfl.vguardrishta.utils.PrefUtil
import io.paperdb.Paper
import javax.inject.Inject

/**
 * Created by Shanmuka on 19-11-2018.
 */
class LogInPresenter @Inject constructor(
    private val context: Context,
    private val authenticateUserCaseCase: AuthenticateUserCaseCase
) : BasePresenter<LogInContract.View>(), LogInContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun onLoginClicked(userName: String, password: String, isRemember: Boolean) {

        if (userName.isEmpty()) {
            getView()?.errorEnterUserName()
            return
        }

        if (password.isEmpty()) {
            getView()?.errorEnterPassword()
            return
        }

        AppUtils.hideKeyboard(getView()?.getActivity()!!)

        if (!AppUtils.isNetworkAvailable(context)) {
            getView()?.showToast(context.getString(R.string.no_internet))
            return
        }

        val credentials = "$userName:$password"
        val authRequest =
            "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        disposables?.add(authenticateUserCaseCase.execute(authRequest).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                saveUser(it, isRemember)
            }, {
                if (it.message!!.contains("401")) {
                    getView()?.showToast(context.getString(R.string.invalidCredentials))
                } else {
                    getView()?.showToast(context.getString(R.string.something_wrong))
                }
            })
        )
    }

    private fun saveUser(user: User?, remember: Boolean) {

//        if (remember) {
//            val creds = Creds()
//            creds.alpha = user!!.userId
//            creds.beta = user.password
//            Paper.book().write(CREDS, creds)
//        } else {
//            Paper.book().write(CREDS, Creds())
//        }

        val prefUtil = PrefUtil(context)
        prefUtil.setIsLoggedIn(true)
        Paper.book().write(KEY_USER, user)

        val userType = user!!.userType

        if (userType == REGIONAL_MANAGER || userType == HEAD_MANAGER ||
            userType == BRANCH_MANAGER || userType == TERRITORY_MANAGER
        ) {
            if (user.mobileNo1 == user.password) {
                getView()?.navigateToChangePassword()
                return
            }
        }

        getView()?.navigateToHome()
    }



}
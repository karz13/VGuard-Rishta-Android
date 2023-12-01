package com.tfl.vguardrishta.ui.components.vguard.fragment.otp

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.AuthenticateUserCaseCase
import com.tfl.vguardrishta.domain.GenerateLoginOtpUseCase
import com.tfl.vguardrishta.domain.ValidateNewMobileUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.extensions.withDelay
import com.tfl.vguardrishta.models.UserCreds
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.models.VguardRishtaUser
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.PrefUtil
import io.paperdb.Paper
import javax.inject.Inject

class OtpVerifyPresenter @Inject constructor(
    val context: Context,
    val validateNewMobileUseCase: ValidateNewMobileUseCase,
    val generateLoginOtpUseCase: GenerateLoginOtpUseCase,
    val authenticateUserCaseCase: AuthenticateUserCaseCase
) : BasePresenter<OtpVerifyContract.View>(), OtpVerifyContract.Presenter {


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun verifyOtp(otp: String) {

        if (otp.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.enter_otp))
            return
        }
        val rishtaUser = CacheUtils.getNewRishtaUser()
        rishtaUser.otp = otp
        CacheUtils.setNewRishtaUser(rishtaUser)

//        getView()?.navigateToNewUserReg() // TODO removeTodo


        disposables?.add(validateNewMobileUseCase.validateOtp(rishtaUser).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it.code == 200) {
                    getView()?.showMsgDialog(it.message)
                    getView()?.navigateToNewUserReg()
                } else {
                    getView()?.showMsgDialog(it.message)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )

    }

    override fun generateOtpForNewUser(otpType: String?) {
        val vguardRishtaUser = CacheUtils.getNewRishtaUser()
        if (vguardRishtaUser.mobileNo.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.enter_mobileNo))
            return
        } else if (!AppUtils.isValidMobileNo(vguardRishtaUser.mobileNo!!)) {
            getView()?.showToast(context.getString(R.string.enter_valid_mobileNo))
            return
        }
        vguardRishtaUser.otpType = otpType

        disposables?.add(validateNewMobileUseCase.execute(vguardRishtaUser).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it.code == 200) {
                    getView()?.showMsgDialog(it.message)
                } else {
                    getView()?.showMsgDialog(it.message)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun verifyLoginOtp(otp: String, loginOtpUserName: String?) {
        if (otp.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.enter_otp))
            return
        }
        val vguardRishtaUser = VguardRishtaUser()
        vguardRishtaUser.otp = otp
        vguardRishtaUser.loginOtpUserName = loginOtpUserName

        val user = UserCreds()
        user.userName = AppUtils.getEncryption().encrypt(loginOtpUserName)
        user.password = AppUtils.getEncryption().encrypt(otp)
        user.authType = Constants.LOGIN_TYPE_OTP
        CacheUtils.writeUserCreds(user)

        disposables?.add(generateLoginOtpUseCase.validateLoginOtp(vguardRishtaUser)
            .applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it.code == 200) {
                    getView()?.showToast(it.message)
                    withDelay(200) {
                        doLoginWitUserNamePassword()
                    }
                } else {
                    getView()?.showMsgDialog(it.message)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )

    }


    private fun doLoginWitUserNamePassword() {
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

    private fun saveUser(user: VguardRishtaUser?, remember: Boolean) {
        val prefUtil = PrefUtil(context)
        prefUtil.setIsLoggedIn(true)
        Paper.book().write(Constants.KEY_RISHTA_USER, user)
        getView()?.navigateToRishtaHome()
    }

    override fun generateOtpForLogin(loginWithOtpUserName: String) {
        val vguardRishtaUser = VguardRishtaUser()
        vguardRishtaUser.loginOtpUserName = loginWithOtpUserName
        disposables?.add(generateLoginOtpUseCase.execute(vguardRishtaUser).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null && it.code == 200) {
                    getView()?.showMsgDialog(it.message)
                } else {
                    getView()?.showMsgDialog(it.message)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }


}
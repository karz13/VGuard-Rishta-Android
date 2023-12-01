package com.tfl.vguardrishta.ui.components.vguard.fragment.login

//import com.tfl.minda.BuildConfig
import android.content.Context
import android.util.Log
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.AppVersionUseCase
import com.tfl.vguardrishta.domain.AuthenticateUserCaseCase
import com.tfl.vguardrishta.domain.GenerateLoginOtpUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.UserCreds
import com.tfl.vguardrishta.models.VguardRishtaUser
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Constants.KEY_RISHTA_USER
import com.tfl.vguardrishta.utils.PrefUtil
import io.paperdb.Paper
import javax.inject.Inject

/**
 * Created by Shanmuka on 19-11-2018.
 */
class LogInFragmentPresenter @Inject constructor(
    private val context: Context,
    private val authenticateUserCaseCase: AuthenticateUserCaseCase,
    private val appVersionUseCase: AppVersionUseCase,
    private val generateLoginOtpUseCase: GenerateLoginOtpUseCase
) : BasePresenter<LogInFragmentContract.View>(), LogInFragmentContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }


    private fun saveUser(user: VguardRishtaUser?, remember: Boolean) {
        val prefUtil = PrefUtil(context)
        prefUtil.setIsLoggedIn(true)
        Paper.book().write(KEY_RISHTA_USER, user)
        CacheUtils.showWelcomeBanner(user?.welcomePointsErrorCode)
        getView()?.navigateToHome()
    }

    override fun getAppVersion() {
        disposables?.add(appVersionUseCase.execute().applySchedulers()
            .doOnSubscribe { }
            .doFinally { }
            .subscribe({ appVersionCode ->
                if (appVersionCode != null) {
                    CacheUtils.setDbAppVersion(appVersionCode.toInt())
                }
                getView()?.showRedirectPlayStore()
            }, {
                Log.e("error", it.message.toString())
            })
        )

    }

    override fun doLogin(
        userName: String,
        password: String,
        loginType: String,
        loginWithOtpUserName: String
    ) {
        if (loginType == Constants.LOGIN_TYPE_PASS) {
            if (userName.isEmpty()) {
                getView()?.errorEnterUserName()
                return
            }

            if (password.isEmpty()) {
                getView()?.errorEnterPassword()
                return
            }

            val user = UserCreds()
            AppUtils.hideKeyboard(getView()?.getActivity()!!)
            user.userName = AppUtils.getEncryption().encrypt(userName)
            user.password = AppUtils.getEncryption().encrypt(password)
            user.authType = loginType
            CacheUtils.writeUserCreds(user)
            doLoginWithUnPass()
        }

        if (loginType == Constants.LOGIN_TYPE_OTP) {
            if (loginWithOtpUserName.isNullOrEmpty()) {
                getView()?.errorEnterOtpLoginNo()
                return
            }
            generateOtpForLogin(loginWithOtpUserName)
        }
    }

    private fun generateOtpForLogin(loginWithOtpUserName: String) {
        val vguardRishtaUser = VguardRishtaUser()
        vguardRishtaUser.loginOtpUserName = loginWithOtpUserName
        disposables?.add(generateLoginOtpUseCase.execute(vguardRishtaUser).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null && it.code == 200) {
                    getView()?.showMsgDialog(it.message)
                    getView()?.navigateToLoginwithOtp(loginWithOtpUserName)
                    CacheUtils.setSendSelectedLang(true)
                } else {
                    getView()?.showMsgDialog(it.message)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun doLoginWithUnPass() {
        disposables?.add(authenticateUserCaseCase.userLoginDetails().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
//                    CacheUtils.setSendSelectedLang(true)
//                    saveUser(it, false)
                    if (it.active != "1" && it.roleId == Constants.RET_USER_TYPE) {
                        getView()?.showErrorDialogMsg(context.getString(R.string.your_account_is_not_active))
                    } else if (it.roleId == CacheUtils.getRishtaUserType()) {

                        //***For AC Engineer
                        if (CacheUtils.getSubUserType() == Constants.AC_SUB_USER_TYPE && it.professionId == 4) {
                            CacheUtils.setSendSelectedLang(true)
                            saveUser(it, false)
                        } else if (CacheUtils.getSubUserType() == Constants.AC_SUB_USER_TYPE) {
                            if (it.professionId == 0) {
                                getView()?.showErrorDialogMsg(context.getString(R.string.select_usertype_retailer))
                            } else if (it.professionId == 1 || it.professionId == 2 || it.professionId == 3) {
                                getView()?.showErrorDialogMsg(context.getString(R.string.select_usertype_plumber))
                            }
                        }
                        //****For Electrical and plumber
                        if (CacheUtils.getSubUserType() == Constants.EC_SUB_USER_TYPE && (it.professionId == 1 || it.professionId == 2 || it.professionId == 3)) {
                            CacheUtils.setSendSelectedLang(true)
                            saveUser(it, false)
                        } else if (CacheUtils.getSubUserType() == Constants.EC_SUB_USER_TYPE) {
                            if (it.professionId == 0) {
                                getView()?.showErrorDialogMsg(context.getString(R.string.select_usertype_retailer))
                            } else if (it.professionId == 4) {
                                getView()?.showErrorDialogMsg(context.getString(R.string.select_usertype_ac))
                            }
                        }

                        //***For Retailer
                        if (CacheUtils.getSubUserType() == Constants.RET_SUB_USER_TYPE && it.professionId == 0) {
                            CacheUtils.setSendSelectedLang(true)
                            saveUser(it, false)
                        } else if (CacheUtils.getSubUserType() == Constants.RET_SUB_USER_TYPE) {
                            if (it.professionId == 4) {
                                getView()?.showErrorDialogMsg(context.getString(R.string.select_usertype_ac))
                            } else if (it.professionId == 1 || it.professionId == 2 || it.professionId == 3) {
                                getView()?.showErrorDialogMsg(context.getString(R.string.select_usertype_plumber))
                            }
                        }

                    } else {
                        if (it.roleId == Constants.INF_USER_TYPE) {
                            if (it.professionId == 4) {
                                getView()?.showErrorDialogMsg(context.getString(R.string.select_usertype_ac))
                            } else if (it.professionId == 1 || it.professionId == 2 || it.professionId == 3) {
                                getView()?.showErrorDialogMsg(context.getString(R.string.select_usertype_plumber))
                            }
                           // getView()?.showErrorDialogMsg(context.getString(R.string.select_electrician_or_plumber))
                        } else {
                            getView()?.showErrorDialogMsg(context.getString(R.string.select_retailer_login_type))
                        }
                    }
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
}
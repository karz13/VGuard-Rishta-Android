package com.tfl.vguardrishta.ui.components.vguard.fragment.loginWithOtp

//import com.tfl.minda.BuildConfig
import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.AppVersionUseCase
import com.tfl.vguardrishta.domain.AuthenticateUserCaseCase
import com.tfl.vguardrishta.domain.GenerateLoginOtpUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.extensions.withDelay
import com.tfl.vguardrishta.models.UserCreds
import com.tfl.vguardrishta.models.VguardRishtaUser
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.PrefUtil
import io.paperdb.Paper
import javax.inject.Inject

/**
 * Created by Shanmuka on 19-11-2018.
 */
class LogInWithOtpPresenter @Inject constructor(
    private val context: Context,
    private val authenticateUserCaseCase: AuthenticateUserCaseCase,
    private val appVersionUseCase: AppVersionUseCase,
    private val generateLoginOtpUseCase: GenerateLoginOtpUseCase
) : BasePresenter<LoginWithOtpContract.View>(), LoginWithOtpContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
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
            })
        )

    }


    override fun createOtp(loginWithOtpUserName: String, otpType: String?) {
        if (loginWithOtpUserName.isNullOrEmpty()) {
            getView()?.errorEnterOtpLoginNo()
            return
        }
        if (!AppUtils.isValidMobileNo(loginWithOtpUserName)) {
            getView()?.showToast(context.getString(R.string.enter_valid_registered_mobile_no))
            return
        }

        generateOtpForLogin(loginWithOtpUserName, otpType)
    }

    private fun generateOtpForLogin(loginWithOtpUserName: String, otpType: String?) {
//        getView()?.showEnterOtp()  // TODO removeTodo

        val vguardRishtaUser = VguardRishtaUser()
        vguardRishtaUser.loginOtpUserName = loginWithOtpUserName
        vguardRishtaUser.otpType = otpType
        disposables?.add(generateLoginOtpUseCase.execute(vguardRishtaUser).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null && it.code == 200) {
                    getView()?.showMsgDialog(it.message)
                    getView()?.showEnterOtp()
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
//                    getView()?.showToast(it.message)
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
        disposables?.add(authenticateUserCaseCase.userLoginDetails().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it.active != "1" && it.roleId == Constants.RET_USER_TYPE) {
                    getView()?.showErrorDialogMsg(context.getString(R.string.your_account_is_not_active))
                } else if (it?.roleId == CacheUtils.getRishtaUserType()) {


                    //***For AC Engineer
                    if(CacheUtils.getSubUserType()==Constants.AC_SUB_USER_TYPE && it.professionId ==4)
                    {
                        saveUser(it, false)
                    }
                    else if (CacheUtils.getSubUserType() == Constants.AC_SUB_USER_TYPE) {
                        if (it.professionId == 0) {
                            getView()?.showErrorDialogMsg(context.getString(R.string.select_usertype_retailer))
                        } else if (it.professionId == 1 || it.professionId == 2 || it.professionId == 3) {
                            getView()?.showErrorDialogMsg(context.getString(R.string.select_usertype_plumber))
                        }
                    }
                    //****For Electrical and plumber
                    if(CacheUtils.getSubUserType()==Constants.EC_SUB_USER_TYPE && (it.professionId ==1 || it.professionId ==2 || it.professionId ==3))
                    {
                        saveUser(it, false)
                    }
                    else if (CacheUtils.getSubUserType() == Constants.EC_SUB_USER_TYPE) {
                        if (it.professionId == 0) {
                            getView()?.showErrorDialogMsg(context.getString(R.string.select_usertype_retailer))
                        } else if (it.professionId == 4) {
                            getView()?.showErrorDialogMsg(context.getString(R.string.select_usertype_ac))
                        }
                    }

                    //***For Retailer
                    if(CacheUtils.getSubUserType()==Constants.RET_SUB_USER_TYPE && it.professionId ==0)
                    {
                        saveUser(it, false)
                    }
                    else if (CacheUtils.getSubUserType() == Constants.RET_SUB_USER_TYPE) {
                        if (it.professionId == 4) {
                            getView()?.showErrorDialogMsg(context.getString(R.string.select_usertype_ac))
                        } else if (it.professionId == 1 || it.professionId == 2 || it.professionId == 3) {
                            getView()?.showErrorDialogMsg(context.getString(R.string.select_usertype_plumber))
                        }
                    }
                }

                else {
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

}
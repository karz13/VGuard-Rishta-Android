package com.tfl.vguardrishta.ui.components.vguard.loginWithOtp

import android.content.Context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GenerateOTPUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.models.User
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import javax.inject.Inject

/**
 * Created by Shanmuka on 3/21/2019.
 */
class LoginWithOtpPresenter @Inject constructor(private val context: Context ,  private val generateOTPUseCase: GenerateOTPUseCase) : BasePresenter<LoginWithOtpContract.View>(),
    LoginWithOtpContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun validateMobileNumber(mobileNo: String) {

        if (mobileNo.isEmpty()){
            getView()?.showToast(context.getString(R.string.enter_mobileNo))
            return
        } else if (!AppUtils.isValidMobileNo(mobileNo)){
            getView()?.showToast(context.getString(R.string.enter_valid_mobileNo))
            return
        }

        getView()?.hideKeyboard()

        val user = User()
        user.otpType = 2
        user.userId = mobileNo
        user.mobileNo1 = mobileNo

        generateOTP(user)

    }

    private fun generateOTP(user: User) {
        disposables?.add(generateOTPUseCase.execute(user).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                navigateToNextScreen(it , user)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun navigateToNextScreen(it: Status, user: User) {
        if (it.code == 200) {
            CacheUtils.setUser(user)
            it.message?.let { it1 -> getView()?.showToast(it1) }
            getView()?.navigateToOtpFragment()
        } else if (it.code == 400) {
            it.message?.let { it1 -> getView()?.showToast(it1) }
        }
    }

}
package com.tfl.vguardrishta.ui.components.vguard.fragment.newuser

import android.view.View
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.models.VguardRishtaUser
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.fragment_new_user_mobile_registration.*
import javax.inject.Inject

class MobileRegistrationFragment :
    BaseFragment<NewUserRegContract.View, NewUserRegContract.Presenter>(),
    NewUserRegContract.View, View.OnClickListener {

    @Inject
    lateinit var newUserRegPresenter: NewUserRegPresenter

    private lateinit var progress: Progress

    override fun initPresenter(): NewUserRegContract.Presenter {
        return newUserRegPresenter
    }

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_new_user_mobile_registration
    }

    override fun initUI() {
        progress = Progress(context, R.string.please_wait)
        btnGetOtp.setOnClickListener(this)
        llOtpThroughCall.setOnClickListener(this)
        CacheUtils.clearUserDetails()
    }


    override fun showProgress() {
        progress.show()
    }

    override fun stopProgress() {
        progress.dismiss()
    }

    override fun showToast(toast: String) {
        activity?.toast(toast)
    }

    override fun showError() {
        activity?.toast(resources.getString(R.string.something_wrong))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnGetOtp -> {
                getInputs(null)
            }

            R.id.llOtpThroughCall -> {
                getInputs(Constants.OTP_TYPE_VOICE)
            }

        }
    }

    private fun getInputs(otpType: String?) {

        var vguardRishtaUser = VguardRishtaUser()
        val checkedRadioButtonId = rgJobProfession.checkedRadioButtonId
        if (checkedRadioButtonId == R.id.rbInfluencer) {
            vguardRishtaUser.retailerInfluencer = "2"
        }
//        else if (checkedRadioButtonId == R.id.rbRetailer) {
//            vguardRishtaUser.retailerInfluencer = "1"
//        }
        vguardRishtaUser.mobileNo = etMobileNumber.text.toString()
        vguardRishtaUser.otpType = otpType
        newUserRegPresenter.generateOtpForNewUser(vguardRishtaUser)

    }

    override fun navigateToOtp() {
        (activity as NewUserRegistrationActivity).navigateToEnterOtp()
    }

    override fun showMsgDialogAndFinish(message: String) {
        AppUtils.showErrorDialog(layoutInflater, context!!, message)
    }
}
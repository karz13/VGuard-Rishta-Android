package com.tfl.vguardrishta.ui.components.vguard.fragment.newuser

import android.view.View
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.addFragment
import com.tfl.vguardrishta.extensions.replaceFragment
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.FileUploader
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.fragment.otp.OtpVerifyFragment
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Progress
import javax.inject.Inject

class NewUserRegistrationActivity :
    BaseActivity<NewUserRegContract.View, NewUserRegContract.Presenter>(), NewUserRegContract.View,
    View.OnClickListener {

    @Inject
    lateinit var newUserRegPresenter: NewUserRegPresenter

    private lateinit var progress: Progress

    override fun initPresenter(): NewUserRegContract.Presenter {
        return newUserRegPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_new_user_registration
    }

    override fun initUI() {
//        setSupportActionBar(toolbar_main)
//        customToolbar.updateToolbar("", "Redeem Points", "")
        progress = Progress(this, R.string.please_wait)
        CacheUtils.setFileUploader(FileUploader())

        replaceFragment(MobileRegistrationFragment(), R.id.content, "")
//        replaceFragment(NewUserKycFragment(), R.id.content, "")
    }


    override fun showProgress() {
        progress.show()
    }

    override fun stopProgress() {
        progress.dismiss()
    }

    override fun showToast(toast: String) {
        toast(toast)
    }

    override fun showError() {
        toast(resources.getString(R.string.something_wrong))
    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

    fun navigateToEnterOtp() {
        addFragment(OtpVerifyFragment(), R.id.content)
    }

    fun navigateToBankAndNominee() {
        addFragment(BankNomineeDetailsFragment(), R.id.content)
    }

    fun navigateToUserPersonalDetails() {
        addFragment(PersonalDetailsFragment(), R.id.content)
    }

    fun navigateUserDetailsPreview() {
        addFragment(PreviewNewUserFragment(), R.id.content)
    }

    fun navigateToEdit() {
        addFragment(PersonalDetailsFragment(), R.id.content)

    }

    fun navigateToNewUserKycScreen() {
        addFragment(NewUserKycFragment(), R.id.content)
    }

}
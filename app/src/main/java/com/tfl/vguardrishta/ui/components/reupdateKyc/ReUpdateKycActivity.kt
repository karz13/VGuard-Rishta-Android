package com.tfl.vguardrishta.ui.components.reupdateKyc

import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.addFragment
import com.tfl.vguardrishta.extensions.replaceFragment
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.fragment.otpForReverify.SendOtpForReVerifyFragment
import kotlinx.android.synthetic.main.activity_reupdate_kyc.*
import javax.inject.Inject

class ReUpdateKycActivity : BaseActivity<ReUpdateKycContract.View, ReUpdateKycContract.Presenter>(),
    ReUpdateKycContract.View {

    @Inject
    lateinit var reUpdateKycPresenter: ReUpdateKycPresenter
    override fun initPresenter(): ReUpdateKycContract.Presenter {
        return reUpdateKycPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_reupdate_kyc
    }

    override fun initUI() {
        replaceFragment(SendOtpForReVerifyFragment(), content.id, "")
        //  replaceFragment(ReUpdateKycFragment(), content.id, "")

    }

    override fun hideKeyBoard() {
    }

    override fun showProgress() {
    }

    override fun stopProgress() {

    }

    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun showError() {
        toast(getString(R.string.something_wrong))
    }

    fun navigateToReUpdateKycFragment() {
        addFragment(ReUpdateKycFragment(), content.id)
    }

    fun navigateToRetReUpdateKycFragment() {
        addFragment(RetailerReUpdateKycFragment(), content.id)
    }

    fun navigateToPreview() {
        addFragment(PreviewReUpdateKycUserFragment(), content.id)
    }

    fun navigateToRetailerKYCPreview() {
        addFragment(PreviewRetailerUpdateKycUserFragment(), content.id)
    }

}
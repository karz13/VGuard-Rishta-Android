package com.tfl.vguardrishta.ui.components.vguard.fragment.profile

import android.view.View
import com.bumptech.glide.Glide
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.VguardRishtaUser
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.vguard.home.RishtaHomeActivity
import com.tfl.vguardrishta.ui.components.vguard.updateProfile.UpdateProfileActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Progress
import io.paperdb.Paper
import kotlinx.android.synthetic.main.fragment_rishta_user_profile.*
import javax.inject.Inject

class RishtaUserProfileFragment :
    BaseFragment<RishtaUserProfileContract.View, RishtaUserProfileContract.Presenter>(),
    RishtaUserProfileContract.View, View.OnClickListener {

    @Inject
    lateinit var ristaUserProfilePresenter: RishtaUserProfilePresenter

    lateinit var vguardRishtaUser: VguardRishtaUser
    private lateinit var progress: Progress

    override fun initPresenter(): RishtaUserProfileContract.Presenter {
        return ristaUserProfilePresenter
    }

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_rishta_user_profile
    }

    override fun initUI() {
        progress = Progress(context, R.string.please_wait)
        (activity as RishtaHomeActivity).updateCustomToolbar(
            Constants.PROFILE,
            getString(R.string.view_profile),
            ""
        )

        val rishtaUser = CacheUtils.getRishtaUser()
        tvEcard.setOnClickListener {
//            AppUtils.openPDF(context!!, vguardRishtaUser.ecardPath)
            AppUtils.openPDF(context!!, rishtaUser.ecardPath)

        }
        cbPanCard.setOnCheckedChangeListener { btn, isChecked ->

            if (isChecked) {

            } else {

            }
        }

        cbBankDetail.setOnCheckedChangeListener { btn, isChecked ->

            if (isChecked) {
                llBankDetails.visibility = View.VISIBLE
                llBankDetails.requestFocus();
            } else {
                llBankDetails.visibility = View.GONE
                llBankDetails.clearFocus()
            }
        }
        tvBankDetails.setOnClickListener {
            cbBankDetail.isChecked = !cbBankDetail.isChecked
        }

        tvNomineeDetails.setOnClickListener {
            cbNominee.isChecked = !cbNominee.isChecked
        }

        cbNominee.setOnCheckedChangeListener { btn, isChecked ->
            if (isChecked) {
                llNomineeDetails.visibility = View.VISIBLE
                llNomineeDetails.requestFocus()
            } else {
                llNomineeDetails.visibility = View.GONE
                llNomineeDetails.clearFocus()

            }
        }

        btnEditProfile.setOnClickListener {
            if (this::vguardRishtaUser.isInitialized) {
                activity?.launchActivity<UpdateProfileActivity> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ristaUserProfilePresenter.getProfile()
    }

    override fun showRishtaUser(it: VguardRishtaUser) {
        Paper.book().write(Constants.KEY_RISHTA_USER, it)
        vguardRishtaUser = it

        var selfie = it.kycDetails.selfie
        selfie = AppUtils.getSelfieUrl() + selfie
        Glide.with(this).load(selfie)
            .placeholder(R.drawable.ic_v_guards_user).into(ivUserImage)
        llCompleteProfile.visibility = View.VISIBLE
        tvDisplayName.text = it.name
        tvUserCode.text = it.userCode
        tvEcard.text = getString(R.string.view_e_card)
        tvPreffLang.text = it.preferredLanguage

        tvGender.text = it.gender
        tvDob.text = it.dob
        tvContactNO.text = it.contactNo
        tvWhatsappNo.text = it.whatsappNo
        tvEmail.text = it.emailId
        tvPermAdd.text = it.permanentAddress
        tvProfession.text = it.profession
        tvEnrolledLyaltyScheme.text = it.enrolledOtherSchemeYesNo

        tvBrandScheme.text = it.otherSchemeBrand
        tvAnnualBusiness.text = it.annualBusinessPotential
        //yesno
        tvSelfie.text = if (!it.kycDetails.selfie.isNullOrEmpty()) {
            "Yes"
        } else {
            "No"
        }
        tvIdDoc.text = if (!it.kycDetails.aadharOrVoterOrDLFront.isNullOrEmpty()) {
            "Yes"
        } else {
            "No"
        }
        tvPanCard.text = if (!it.kycDetails.panCardFront.isNullOrEmpty()) {
            "Yes"
        } else {
            "No"
        }
        tvBankDetails.text = if (!it.bankDetail?.bankAccNo.isNullOrEmpty()) {
            "Yes"
        } else {
            "No"
        }

        //bank detail
        tvAccountNo.text = it.bankDetail?.bankAccNo
        tvName.text = it.bankDetail?.bankAccHolderName
        tvBank.text = it.bankDetail?.bankNameAndBranch
        tvIfsc.text = it.bankDetail?.bankIfsc?.trim()
//yes no
        tvNomineeDetails.text = if (!it.bankDetail?.nomineeName.isNullOrEmpty()) {
            "Yes"
        } else {
            "No"
        }

        //nom details

        tvNomineeName.text = it.bankDetail?.nomineeName
        tvNomMobile.text = it.bankDetail?.nomineeMobileNo
        tvNomEmail.text = it.bankDetail?.nomineeEmail
        tvNomineeAddress.text = it.bankDetail?.nomineeAdd
        tvNomineeRelation.text = it.bankDetail?.nomineeRelation

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

        }
    }


}
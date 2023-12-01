package com.tfl.vguardrishta.ui.components.reupdateKyc

import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.remote.ApiService
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.logIn.LogInActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.fragment_preview_reupdate_kyc_user.*
import java.io.File
import javax.inject.Inject

class PreviewReUpdateKycUserFragment :
    BaseFragment<ReUpdateKycContract.View, ReUpdateKycContract.Presenter>(),
    ReUpdateKycContract.View,
    View.OnClickListener, ActivityFinishListener {

    @Inject
    lateinit var newUserRegPresenter: ReUpdateKycPresenter

    private lateinit var progress: Progress

    override fun initPresenter(): ReUpdateKycContract.Presenter {
        return newUserRegPresenter
    }

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_preview_reupdate_kyc_user
    }

    override fun initUI() {
        progress = Progress(context, R.string.please_wait)
        setInputs()
        btnEdit.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
    }

    override fun hideKeyBoard() {

    }

    private fun setInputs() {
        val rishtaUser = CacheUtils.getRishtaUser()
        val selfieUrl = AppUtils.getSelfieUrl() + rishtaUser.kycDetails.selfie

        etPreferredLanguage.setText(rishtaUser.preferredLanguage)
        etReferralCode.setText(rishtaUser.referralCode)
        etNameOfReferee.setText(rishtaUser.nameOfReferee)
        etName.setText(rishtaUser.name)
        etGender.setText(rishtaUser.gender)
        tvDob.setText(rishtaUser.dob)
        etContactNumber.setText(rishtaUser.contactNo)
        etWhatsAppNumber.setText(rishtaUser.whatsappNo)
        etEmail.setText(rishtaUser.emailId)
        etPermanentAddress.setText(rishtaUser.permanentAddress)
        etStreetLocality.setText(rishtaUser.streetAndLocality)
        etLandmark.setText(rishtaUser.landmark)
        if (rishtaUser.otherCity.isNullOrEmpty()) {
            etCity.setText(rishtaUser.city)
        } else {
            etCity.setText(rishtaUser.otherCity)
        }

        etDistrict.setText(rishtaUser.dist)
        etState.setText(rishtaUser.state)
        etPinCode.setText(rishtaUser.pinCode)

        if (rishtaUser.addDiff == 1) {
            llCurrentAdd.visibility = View.VISIBLE
        } else {
            llCurrentAdd.visibility = View.GONE
        }

        etCurrentAddress.setText(rishtaUser.currentAddress)
        etCurrentStreetLocality.setText(rishtaUser.currStreetAndLocality)
        etCurrentLandmark.setText(rishtaUser.currLandmark)

        if (rishtaUser.otherCurrCity.isNullOrEmpty()) {
            etCurrCity.setText(rishtaUser.currCity)
        } else {
            etCurrCity.setText(rishtaUser.otherCurrCity)
        }

        etCurrDistrict.setText(rishtaUser.currDist)
        etCurrState.setText(rishtaUser.currState)
        etCurrentPinCode.setText(rishtaUser.currPinCode)

        etJobProfession.setText(rishtaUser.profession)
        etSubProfession.setText(rishtaUser.subProfession)
        etMaritalStatus.setText(rishtaUser.maritalStatus)


        isEnrolledOtherScheme.setText(
            if (rishtaUser.enrolledOtherScheme == 1) {
                "Yes"
            } else {
                "No"
            }
        )

        if (rishtaUser.enrolledOtherScheme == 1) {
            llOtherSchemeEnrolledDetails.visibility = View.VISIBLE
            etOtherSchemeBrandName.setText(rishtaUser.otherSchemeBrand)
            etWhatLikedAbtOtherScheme.setText(rishtaUser.abtOtherSchemeLiked)

            if (!rishtaUser.otherSchemeBrand2.isNullOrEmpty()) {
                llOtherSchemeEnrolledDetails2.visibility = View.VISIBLE
                etOtherSchemeBrandName2.setText(rishtaUser.otherSchemeBrand2)
                etWhatLikedAbtOtherScheme2.setText(rishtaUser.abtOtherSchemeLiked2)

            }

            if (!rishtaUser.otherSchemeBrand3.isNullOrEmpty()) {
                llOtherSchemeEnrolledDetails3.visibility = View.VISIBLE
                etOtherSchemeBrandName3.setText(rishtaUser.otherSchemeBrand3)
                etWhatLikedAbtOtherScheme3.setText(rishtaUser.abtOtherSchemeLiked3)
            }

            if (!rishtaUser.otherSchemeBrand4.isNullOrEmpty()) {
                llOtherSchemeEnrolledDetails4.visibility = View.VISIBLE
                etOtherSchemeBrandName4.setText(rishtaUser.otherSchemeBrand4)
                etWhatLikedAbtOtherScheme4.setText(rishtaUser.abtOtherSchemeLiked4)
            }

            if (!rishtaUser.otherSchemeBrand5.isNullOrEmpty()) {
                llOtherSchemeEnrolledDetails5.visibility = View.VISIBLE
                etOtherSchemeBrandName5.setText(rishtaUser.otherSchemeBrand5)
                etWhatLikedAbtOtherScheme5.setText(rishtaUser.abtOtherSchemeLiked5)
            }
        }

        etAnnualBusiness.setText(rishtaUser.annualBusinessPotential)
        etIdProofType.setText(rishtaUser.kycDetails.kycIdName)

        val selfie = CacheUtils.getFileUploader().getUserPhotoFile()
        setImageForProofs(selfie, selfieUrl, ivSelfie, llSelfie)

        val idProofFront = CacheUtils.getFileUploader().getIdProofFileFront()
        val idFrontUrl = AppUtils.getIdCardUrl() + rishtaUser.kycDetails.aadharOrVoterOrDLFront
        setImageForProofs(idProofFront, idFrontUrl, ivIdFront, llIdFront)


        val idProofBack = CacheUtils.getFileUploader().getIdProofBackFile()
        val idBackUrl = AppUtils.getIdCardUrl() + rishtaUser.kycDetails.aadharOrVoterOrDlBack
        setImageForProofs(idProofBack, idBackUrl, ivIdBack, llIdBack)

        etIdNoManualEntered.setText(rishtaUser.kycDetails.aadharOrVoterOrDlNo)

        val panCardFront = CacheUtils.getFileUploader().getPanCardFrontFile()
        val panCardUrl = ApiService.panCardUrl + rishtaUser.kycDetails.panCardFront
        setImageForProofs(panCardFront, panCardUrl, ivPanCardFront, llPanFront)

        etPanNoManualEntered.setText(rishtaUser.kycDetails.panCardNo)

        etAccountNumber.setText(rishtaUser.bankDetail?.bankAccNo)
        etAccountHolderName.setText(rishtaUser.bankDetail?.bankAccHolderName)
        etTypeOfAccount.setText(rishtaUser.bankDetail?.bankAccType)
        etBankNameBranch.setText(rishtaUser.bankDetail?.bankNameAndBranch)
        etIfscCode.setText(rishtaUser.bankDetail?.bankIfsc)

        val checkBookFile = CacheUtils.getFileUploader().getChequeBookPhotoFile()
        val checkPhotoUrl = AppUtils.getChequeUrl() + rishtaUser.bankDetail?.checkPhoto
        setImageForProofs(checkBookFile, checkPhotoUrl, ivChequePhoto, llCheckPhoto)

        etNameOfNominee.setText(rishtaUser.bankDetail?.nomineeName)
        tvNomineeDob.setText(rishtaUser.bankDetail?.nomineeDob)
        etNomineeMobileNo.setText(rishtaUser.bankDetail?.nomineeMobileNo)
        etNomineeEmail.setText(rishtaUser.bankDetail?.nomineeEmail)
        etNomineeAddress.setText(rishtaUser.bankDetail?.nomineeAdd)
        etNomineeRelationship.setText(rishtaUser.bankDetail?.nomineeRelation)


    }

    private fun setImageForProofs(
        file: File?,
        proofUrl: String,
        imageView: ImageView,
        linearLayout: LinearLayout
    ) {
        if (file != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageView.setImageURI(Uri.parse(file.toString()));
            } else {
                imageView.setImageURI(Uri.fromFile(file));
            }
        } else {
            Glide.with(this).load(proofUrl)
                .placeholder(R.drawable.no_image).into(imageView)
        }

        imageView.setOnClickListener {
            if (file != null) {
                FileUtils.zoomFileImage(activity!!, file)
            } else
                FileUtils.zoomImage(activity!!, proofUrl!!)
        }

        linearLayout.setOnClickListener {
            if (file != null) {
                FileUtils.zoomFileImage(activity!!, file)
            } else
                FileUtils.zoomImage(activity!!, proofUrl!!)
        }
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
            R.id.btnEdit -> {
                activity?.supportFragmentManager?.popBackStack()
            }

            R.id.btnSubmit -> {
                newUserRegPresenter.registerUser()
            }
        }
    }

    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialogWithFinish(layoutInflater, context!!, message, this)
    }

    override fun navigateToLogin() {
        PrefUtil(context!!).setIsLoggedIn(false)
        CacheUtils.clearUserDetails()
        activity?.finish()
        activity?.finishAffinity()
        activity?.launchActivity<LogInActivity> { }
    }

    override fun finishView() {
        navigateToLogin()
    }
}
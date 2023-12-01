package com.tfl.vguardrishta.ui.components.vguard.fragment.newuser

import android.net.Uri
import android.os.Build
import android.view.View
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.logIn.LogInActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.fragment_preview_registration.*
import javax.inject.Inject

class PreviewNewUserFragment :
    BaseFragment<NewUserRegContract.View, NewUserRegContract.Presenter>(), NewUserRegContract.View,
    View.OnClickListener, ActivityFinishListener {

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
        return R.layout.fragment_preview_registration
    }

    override fun initUI() {
        progress = Progress(context, R.string.please_wait)
        setInputs()
        btnEdit.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
    }

    private fun setInputs() {
        val rishtaUser = CacheUtils.getNewRishtaUser()

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
        etCity.setText(rishtaUser.city)
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
        etCurrCity.setText(rishtaUser.currCity)
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
        if (selfie != null) {
//            tvSelfie.setText(idProofFront?.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ivSelfie.setImageURI(Uri.parse(selfie.toString()));
            } else {
                ivSelfie.setImageURI(Uri.fromFile(selfie));
            }
            llSelfie.setOnClickListener { FileUtils.zoomFileImage(context!!, selfie) }
        }

        val idProofFront = CacheUtils.getFileUploader().getIdProofFileFront()
        if (idProofFront != null) {
//            tvIdFront.setText(idProofFront?.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ivIdFront.setImageURI(Uri.parse(idProofFront.toString()));
            } else {
                ivIdFront.setImageURI(Uri.fromFile(idProofFront));
            }
            llIdFront.setOnClickListener { FileUtils.zoomFileImage(context!!, idProofFront) }
        }

        val idProofBack = CacheUtils.getFileUploader().getIdProofBackFile()
        if (idProofBack != null) {
//            tvIdBack.setText(idProofBack?.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ivIdBack.setImageURI(Uri.parse(idProofBack.toString()));
            } else {
                ivIdBack.setImageURI(Uri.fromFile(idProofBack));
            }
            llIdBack.setOnClickListener { FileUtils.zoomFileImage(context!!, idProofBack) }
        }

        etIdNoManualEntered.setText(rishtaUser.kycDetails.aadharOrVoterOrDlNo)

        val panCardFront = CacheUtils.getFileUploader().getPanCardFrontFile()
        if (panCardFront != null) {
//            tvPanCardFront.setText(panCardFront?.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ivPanCardFront.setImageURI(Uri.parse(panCardFront.toString()));
            } else {
                ivPanCardFront.setImageURI(Uri.fromFile(panCardFront));
            }
            llPanFront.setOnClickListener { FileUtils.zoomFileImage(context!!, panCardFront) }
        }

        val panCardBack = CacheUtils.getFileUploader().getPanCardBackFile()
        if (panCardBack != null) {
//            tvPanCardBack.setText(panCardBack?.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ivPanCardBack.setImageURI(Uri.parse(panCardBack.toString()));
            } else {
                ivPanCardBack.setImageURI(Uri.fromFile(panCardBack));
            }
            llPanBack.setOnClickListener { FileUtils.zoomFileImage(context!!, panCardBack) }
        }
        etPanNoManualEntered.setText(rishtaUser.kycDetails.panCardNo)

        etAccountNumber.setText(rishtaUser.bankDetail?.bankAccNo)
        etAccountHolderName.setText(rishtaUser.bankDetail?.bankAccHolderName)
        etTypeOfAccount.setText(rishtaUser.bankDetail?.bankAccType)
        etBankNameBranch.setText(rishtaUser.bankDetail?.bankNameAndBranch)
        etIfscCode.setText(rishtaUser.bankDetail?.bankIfsc)
        val checkBookFile = CacheUtils.getFileUploader().getChequeBookPhotoFile()
        if (checkBookFile != null) {
//            tvChequePhoto.setText(checkBookFile?.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ivChequePhoto.setImageURI(Uri.parse(checkBookFile.toString()));
            } else {
                ivChequePhoto.setImageURI(Uri.fromFile(checkBookFile));
            }
            llCheckPhoto.setOnClickListener { FileUtils.zoomFileImage(context!!, checkBookFile) }
        }
        etNameOfNominee.setText(rishtaUser.bankDetail?.nomineeName)
        tvNomineeDob.setText(rishtaUser.bankDetail?.nomineeDob)
        etNomineeMobileNo.setText(rishtaUser.bankDetail?.nomineeMobileNo)
        etNomineeEmail.setText(rishtaUser.bankDetail?.nomineeEmail)
        etNomineeAddress.setText(rishtaUser.bankDetail?.nomineeAdd)
        etNomineeRelationship.setText(rishtaUser.bankDetail?.nomineeRelation)

        if (rishtaUser.addDiff == 1) {
            llCurrentAdd.visibility = View.GONE

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
                (activity as NewUserRegistrationActivity).navigateToEdit()
            }

            R.id.btnSubmit -> {
                newUserRegPresenter.registerUser()
            }
        }
    }

    override fun showMsgDialogAndFinish(message: String) {
        AppUtils.showErrorDialogWithFinish(layoutInflater, context!!, message, this)
    }

    override fun showErrorDialog(message: String) {
        AppUtils.showErrorDialog(layoutInflater, context!!, message)
    }

    override fun navigateToLogin() {
        activity?.finish()
        activity?.finishAffinity()
        activity?.launchActivity<LogInActivity> { }
    }

    override fun finishView() {
        navigateToLogin()
    }
}
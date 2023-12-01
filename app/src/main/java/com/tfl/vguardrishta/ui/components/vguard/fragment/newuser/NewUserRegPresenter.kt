package com.tfl.vguardrishta.ui.components.vguard.fragment.newuser

import android.content.Context
import android.os.AsyncTask
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.*
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.FileUtils
import javax.inject.Inject

class NewUserRegPresenter @Inject constructor(
    val context: Context,
    val registerUseCase: NewUserRegisterUseCase,
    val getKycIdTypesUseCase: GetKycIdTypesUseCase,
    val validateNewMobileUseCase: ValidateNewMobileUseCase,
    val stateMasterUseCase: StateMasterUseCase,
    val districtMasterUseCase: DistrictMasterUseCase,
    val cityMasterUseCase: CityMasterUseCase,
    val bankTransferUseCase: BankTransferUseCase,
    val professionTypesUseCase: ProfessionTypesUseCase

) : BasePresenter<NewUserRegContract.View>(), NewUserRegContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }


    override fun getBanks() {
        disposables?.add(bankTransferUseCase.getBanks().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    processBanks(it)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun processBanks(it: List<BankDetail>?) {
        val arrayList = CacheUtils.getFirstBank(context)
        if (!it.isNullOrEmpty()) {
            arrayList.addAll(it)
        }
        CacheUtils.setBanks(arrayList)
        getView()?.showBanks(arrayList)
    }

    override fun generateOtpForNewUser(vguardRishtaUser: VguardRishtaUser) {
        if (vguardRishtaUser.retailerInfluencer.isNullOrEmpty()) {
            getView()?.showToast(context.resources.getString(R.string.select_job_profession))
            return
        }

        if (vguardRishtaUser.mobileNo.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.enter_mobileNo))
            return
        } else if (!AppUtils.isValidMobileNo(vguardRishtaUser.mobileNo!!)) {
            getView()?.showToast(context.getString(R.string.enter_valid_mobileNo))
            return
        }

        CacheUtils.setNewRishtaUser(vguardRishtaUser)
//        getView()?.navigateToOtp() // TODO removeTodo

        vguardRishtaUser.preferredLanguagePos = AppUtils.getSelectedLangId().toString()
        disposables?.add(validateNewMobileUseCase.execute(vguardRishtaUser).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it.code == 200) {
                    getView()?.showMsgDialogAndFinish(it.message)
                    getView()?.navigateToOtp()
                } else {
                    getView()?.showMsgDialogAndFinish(it.message)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )

    }

    override fun getReferalName(code: String) {
        disposables?.add(registerUseCase.getReferralName(code).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null && !it.nameOfReferee.isNullOrEmpty()) {
                    getView()?.showNameOfReferee(it.nameOfReferee!!)
                } else {
                    getView()?.showNameOfReferee("")
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )

    }

    override fun getDistrict(id: Long?) {
        disposables?.add(districtMasterUseCase.execute(id!!).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processDistMaster(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }


    fun verifyNewUserData(rishtaUser: VguardRishtaUser) {

        if (rishtaUser.preferredLanguage.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.enter_preffered_lang))
            return
        }

        if (rishtaUser.name.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.enter_name))
            return
        }

        if (rishtaUser.gender.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.select_gender))
            return
        }


        if (rishtaUser.dob.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.choose_dob))
            return
        }

        if (rishtaUser.contactNo.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.enter_contact_no))
            return
        }

        if (rishtaUser.isWhatsAppSame == null) {
            getView()?.showToast(context.getString(R.string.please_select_whats_app))
            return
        }

//        if (rishtaUser.isWhatsAppSame != null && rishtaUser.isWhatsAppSame == 0) {
//            if (rishtaUser.whatsappNo.isNullOrEmpty()) {
//                getView()?.showToast(context.getString(R.string.please_enter_whatsapp))
//                return
//            }
//        }

        if (!rishtaUser.whatsappNo.isNullOrEmpty() && !AppUtils.isValidMobileNo(rishtaUser.whatsappNo.toString())) {
            getView()?.showToast(context.getString(R.string.please_enter_valid_whatsapp))
            return
        }

        if (!rishtaUser.emailId.isNullOrEmpty() && !AppUtils.isValidEmailAddress(rishtaUser.emailId!!)) {
            getView()?.showToast(context.getString(R.string.enter_valid_mail))
            return
        }

        if (rishtaUser.permanentAddress.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_perm_address))
            return
        }

        if (rishtaUser.streetAndLocality.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_street))
            return
        }

        if (rishtaUser.pinCodeId.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_pincode_to_get_state_and_dist))
            return
        }

        if (rishtaUser.pinCode.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_pincode))
            return
        }
        if (!AppUtils.isValidPinCode(rishtaUser.pinCode!!)) {
            getView()?.showToast(context.getString(R.string.enter_valid_pincode))
            return
        }


        if (rishtaUser.stateId == null || rishtaUser.stateId == "-1") {
            getView()?.showToast(context.getString(R.string.please_select_state))
            return
        }

        if (rishtaUser.distId == null || rishtaUser.distId == "-1") {
            getView()?.showToast(context.getString(R.string.please_select_dist))
            return
        }

        if (rishtaUser.cityId == "-1") {
            getView()?.showToast(context.getString(R.string.select_city))
            return
        }

        if (rishtaUser.cityId == "-2" && rishtaUser.otherCity.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_other_city))
            return
        }




        CacheUtils.setNewRishtaUser(rishtaUser)



        getView()?.navigateToNewUserKyc()
    }

    override fun verifyNewUserKycData(rishtaUser: VguardRishtaUser) {

        if (rishtaUser.addDiff == 1) {
            if (rishtaUser.currentAddress.isNullOrEmpty()) {
                getView()?.showToast(context.getString(R.string.please_enter_curr_address))
                return
            }

            if (rishtaUser.currStreetAndLocality.isNullOrEmpty()) {
                getView()?.showToast(context.getString(R.string.please_enter_street))
                return
            }


//            if (rishtaUser.currCity.isNullOrEmpty()) {
//                getView()?.showToast(context.getString(R.string.please_enter_city))
//                return
//            }

            if (rishtaUser.currPinCodeId.isNullOrEmpty()) {
                getView()?.showToast(context.getString(R.string.please_enter_pincode_to_get_state_and_dist))
                return
            }

            if (rishtaUser.currPinCode.isNullOrEmpty()) {
                getView()?.showToast(context.getString(R.string.please_enter_pincode))
                return
            }
            if (!AppUtils.isValidPinCode(rishtaUser.currPinCode!!)) {
                getView()?.showToast(context.getString(R.string.enter_valid_pincode))
                return
            }

            if (rishtaUser.currStateId == null || rishtaUser.currStateId == "-1") {
                getView()?.showToast(context.getString(R.string.please_select_state))
                return
            }

            if (rishtaUser.currDistId == null || rishtaUser.currDistId == "-1") {
                getView()?.showToast(context.getString(R.string.please_select_dist))
                return
            }

            if (rishtaUser.currCityId == "-1") {
                getView()?.showToast(context.getString(R.string.select_city))
                return
            }

            if (rishtaUser.currCityId == "-2" && rishtaUser.otherCurrCity.isNullOrEmpty()) {
                getView()?.showToast(context.getString(R.string.please_enter_other_city))
                return
            }
        }

        if (rishtaUser.addDiff == -1) {
            getView()?.showToast(context.getString(R.string.select_is_current_address_different))
            return
        }

        if (rishtaUser.professionId == null || rishtaUser.professionId == -1) {
            getView()?.showToast(context.getString(R.string.select_profession))
            return
        }

        if (rishtaUser.maritalStatusId == null) {
            getView()?.showToast(context.getString(R.string.select_marital_status))
            return
        }

        if (rishtaUser.enrolledOtherScheme == null) {

            getView()?.showToast(context.getString(R.string.select_already_enrolled_into_loyalty_scheme))
            return
        }

        if (rishtaUser.enrolledOtherScheme == 1) {
            if (rishtaUser.otherSchemeBrand.isNullOrEmpty()) {
                getView()?.showToast(context.getString(R.string.fill_if_yes_please_mention_scheme_and_brand_name))
                return
            }
            if (rishtaUser.abtOtherSchemeLiked.isNullOrEmpty()) {
                getView()?.showToast(context.getString(R.string.fill_if_yes_what_you_liked_about_the_program))
                return
            }
        }

        if (rishtaUser.annualBusinessPotential.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_mention_your_annual_business_potential))
            return
        }

        val userPhoto = CacheUtils.getFileUploader().getUserPhotoFile()
        if (userPhoto == null) {
            getView()?.showToast(context.getString(R.string.please_click_your_selfie))
            return
        }



        if (rishtaUser.kycDetails.kycId == null || rishtaUser.kycDetails.kycId == -1) {
            getView()?.showToast(context.getString(R.string.select_idcard_type))
            return
        }

        val idFileFront = CacheUtils.getFileUploader().getIdProofFileFront()
        if (idFileFront == null) {
            getView()?.showToast(context.getString(R.string.upload_id_proof_front))
            return
        }

        val idFileBack = CacheUtils.getFileUploader().getIdProofBackFile()
        if (idFileBack == null) {
            getView()?.showToast(context.getString(R.string.upload_id_proof_back))
            return
        }

        if (rishtaUser.kycDetails.aadharOrVoterOrDlNo.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_id_no_manually))
            return
        }else if (!AppUtils.isAadhaarValid(rishtaUser.kycDetails.aadharOrVoterOrDlNo!!)){
            getView()?.showToast(context.getString(R.string.please_enter_valid_aadhar_no))
            return
        }

       /* if(rishtaUser.panImage.isNullOrEmpty()){
            val panFileFront = CacheUtils.getFileUploader().getPanCardFrontFile()
            if (panFileFront == null) {
                getView()?.showToast(context.getString(R.string.upload_pan_card_front))
                return
            }
        }
        if (rishtaUser.panNumber.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_pannumber))
            return

        }*/

        if (!rishtaUser.panNumber.isNullOrEmpty()) {
            if (!AppUtils.isValidPanNo(rishtaUser.panNumber)) {
                getView()?.showToast(context.getString(R.string.please_enter_valid_pan_no))
                return

            }
        }



        getView()?.navigateToBankAndNomineeDetails()
    }

    override fun getStates() {
        disposables?.add(stateMasterUseCase.execute().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processStateMasters(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )

    }

    override fun getProfessions() {
        disposables?.add(professionTypesUseCase.getProfessions(3).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processProfession(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun getSubProfessions(professionId: String) {
        disposables?.add(professionTypesUseCase.getSubProfessions(professionId).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processSubProfession(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }


    override fun getCities(id: Long?) {
        disposables?.add(cityMasterUseCase.execute(id!!).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processCityMaster(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun verifyDetails(rishtaUser: VguardRishtaUser) {

        val nomineeEmail = rishtaUser.bankDetail?.nomineeEmail;
        val nomineeMobileNo = rishtaUser.bankDetail?.nomineeMobileNo

        if (!nomineeMobileNo.isNullOrEmpty() && !AppUtils.isValidMobileNo(nomineeMobileNo)) {
            getView()?.showToast(context.getString(R.string.enter_valid_mobileNo))
            return
        }

        if (checkBankDetails(rishtaUser)) {
            getView()?.showToast(context.getString(R.string.please_fill_your_complete_bank_details))
            return
        }

        if (checkNomineeDetails(rishtaUser)) {
            getView()?.showToast(context.getString(R.string.please_fill_complete_nominee_details))
            return
        }

        if (!nomineeEmail.isNullOrEmpty()
            && !AppUtils.isValidEmailAddress(nomineeEmail)
        ) {
            getView()?.showToast(context.getString(R.string.enter_valid_mail))
            return
        }


        CacheUtils.setNewRishtaUser(rishtaUser)
        getView()?.showUserDetailsPreview()
    }

    override fun getStateDistByPinCode(pinCode: String) {
        disposables?.add(stateMasterUseCase.getDetailsByPinCode(pinCode!!).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                getView()?.setDetailsByPinCode(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun getStateDistCitiesByPincodeId(pinCodeId: String) {
        disposables?.add(stateMasterUseCase.getStateDistCitiesByPincodeId(pinCodeId!!)
            .applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                getView()?.setCitiesByPinCode(it)
                processForStateAndDist(it)
                processCityMaster(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun processForStateAndDist(it: List<CityMaster>) {
        val stateList = arrayListOf<StateMaster>()
        val distList = arrayListOf<DistrictMaster>()
        if (!it.isNullOrEmpty()) {
            val stateMaster = StateMaster()
            stateMaster.stateName = it[0].stateName
            stateMaster.id = it[0].stateId
            stateList.add(stateMaster)

            val districtMaster = DistrictMaster()
            districtMaster.districtName = it[0].districtName
            districtMaster.id = it[0].districtId
            distList.add(districtMaster)
        }
        processStateMasters(stateList)
        processDistMaster(distList)
    }

    override fun getPincodeList(pinCode: String) {
        disposables?.add(stateMasterUseCase.getPincodeList(pinCode!!).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                getView()?.setPinCodeList(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun checkBankDetails(rishtaUser: VguardRishtaUser): Boolean {
        val bankDetail = rishtaUser.bankDetail

        var isCheckPhotoAvailable = checkBankCheckAvaialbe(rishtaUser)



        if (!bankDetail?.bankNameAndBranch.isNullOrEmpty() || !bankDetail?.bankAccType.isNullOrEmpty()
            || !bankDetail?.bankAccHolderName.isNullOrEmpty() || !bankDetail?.bankAccNo.isNullOrEmpty()
            || !bankDetail?.bankIfsc.isNullOrEmpty() || isCheckPhotoAvailable
        ) {
            if (!bankDetail?.bankNameAndBranch.isNullOrEmpty() && !bankDetail?.bankAccType.isNullOrEmpty()
                && !bankDetail?.bankAccHolderName.isNullOrEmpty() && !bankDetail?.bankAccNo.isNullOrEmpty()
                && !bankDetail?.bankIfsc.isNullOrEmpty() && isCheckPhotoAvailable
            ) {
                return false
            }

            return true
        }

        return false
    }

    private fun checkNomineeDetails(rishtaUser: VguardRishtaUser): Boolean {
        val bankDetail = rishtaUser.bankDetail
        if (bankDetail?.nomineeName.isNullOrEmpty() || bankDetail?.nomineeDob.isNullOrEmpty()
            || bankDetail?.nomineeMobileNo.isNullOrEmpty()  /*|| bankDetail?.nomineeEmail.isNullOrEmpty()*/
            || bankDetail?.nomineeAdd.isNullOrEmpty() || bankDetail?.nomineeRelation.isNullOrEmpty()
        ) {
            return true
        }

        /* if (!bankDetail?.nomineeName.isNullOrEmpty() || !bankDetail?.nomineeDob.isNullOrEmpty()
             || !bankDetail?.nomineeMobileNo.isNullOrEmpty() || !bankDetail?.nomineeEmail.isNullOrEmpty()
             || !bankDetail?.nomineeAdd.isNullOrEmpty() || !bankDetail?.nomineeRelation.isNullOrEmpty()
         ) {
             if (!bankDetail?.nomineeName.isNullOrEmpty() && !bankDetail?.nomineeDob.isNullOrEmpty()
                 && !bankDetail?.nomineeMobileNo.isNullOrEmpty() && !bankDetail?.nomineeEmail.isNullOrEmpty()
                 && !bankDetail?.nomineeAdd.isNullOrEmpty() && !bankDetail?.nomineeRelation.isNullOrEmpty()

             ) {
                 return false
             }

             return true
         }*/

        return false
    }

    private fun checkBankCheckAvaialbe(rishtaUser: VguardRishtaUser): Boolean {
        val bankDetail = rishtaUser.bankDetail
        if (!bankDetail?.checkPhoto.isNullOrEmpty()) {
            return true
        }
        val checkPhoto = CacheUtils.getFileUploader().getChequeBookPhotoFile()
        if (checkPhoto != null) {
            return true
        }
        return false
    }


    override fun registerUser() {
        val rishtaUser = CacheUtils.getNewRishtaUser()
        uploadPhotos(rishtaUser)
    }

    private fun uploadPhotos(ru: VguardRishtaUser) {
        object : AsyncTask<Void, Void, Status>() {

            override fun onPreExecute() {
                getView()?.showProgress()
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): com.tfl.vguardrishta.models.Status? {

                val checkBook = CacheUtils.getFileUploader().getChequeBookPhotoFile()
                if (checkBook != null) {
                    val entityUid = FileUtils.sendImageFromOutSide(checkBook, Constants.CHEQUE, "")
                    if (!entityUid.isNullOrEmpty()) {
                        ru.bankDetail?.checkPhoto = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }
                val userPhoto = CacheUtils.getFileUploader().getUserPhotoFile()
                if (userPhoto != null) {
                    val entityUid = FileUtils.sendImageFromOutSide(userPhoto, Constants.PROFILE, "")
                    if (!entityUid.isNullOrEmpty()) {
                        ru.kycDetails.selfie = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }

                val idFileFront = CacheUtils.getFileUploader().getIdProofFileFront()
                if (idFileFront != null) {
                    val entityUid = FileUtils.sendImageFromOutSide(idFileFront, Constants.ID_CARD_FRONT, "")
                    if (!entityUid.isNullOrEmpty()) {
                        ru.kycDetails.aadharOrVoterOrDLFront = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }

                val idFileBack = CacheUtils.getFileUploader().getIdProofBackFile()
                if (idFileBack != null) {
                    val entityUid =
                        FileUtils.sendImageFromOutSide(idFileBack, Constants.ID_CARD_BACK, "")
                    if (!entityUid.isNullOrEmpty()) {
                        ru.kycDetails.aadharOrVoterOrDlBack = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }

                val pandCardFront = CacheUtils.getFileUploader().getPanCardFrontFile()
                if (pandCardFront != null) {
                    val entityUid =
                        FileUtils.sendImageFromOutSide(pandCardFront, Constants.PAN_CARD_FRONT, "")
                    if (!entityUid.isNullOrEmpty()) {
                        ru.kycDetails.panCardFront = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }

                val panCardBackFile = CacheUtils.getFileUploader().getPanCardBackFile()
                if (panCardBackFile != null) {
                    val entityUid = FileUtils.sendImageFromOutSide(
                        panCardBackFile,
                        Constants.PAN_CARD_BACK, ""!!
                    )
                    if (!entityUid.isNullOrEmpty()) {
                        ru.kycDetails.panCardBack = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }
                return Status(200, "SUCCESS")

            }

            override fun onPostExecute(result: com.tfl.vguardrishta.models.Status?) {
                getView()?.stopProgress()
                if (result!!.code == 200) {
                    sendUser(ru)
                } else {
                    getView()?.showToast(context.getString(R.string.file_upload_issue))
                }
            }
        }.execute()
    }

    private fun sendUser(ru: VguardRishtaUser) {
        disposables?.add(registerUseCase.execute(ru).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it.code == 200) {
                    getView()?.showMsgDialogAndFinish(it.message)
                }/* else if(it.code == 0) {
                    getView()?.showErrorDialog(it.message)
                }*/ else {
                    getView()?.showErrorDialog(it.message)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun processResult(it: Status?) {
        if (it == null) {
            getView()?.showToast(context.getString(R.string.problem_occurred))
            return
        }
        if (it.code == 200) {
            getView()?.showToast(it.message!!)
        } else if (it.code == 400) {
            getView()?.showToast(it.message!!)
        }
    }

    private fun processStateMasters(it: List<StateMaster>?) {
        val arrayList = CacheUtils.getFirstState(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        CacheUtils.setStates(arrayList)
        getView()?.setStatesSpinner(arrayList)
    }

    private fun processCityMaster(it: List<CityMaster>?) {
        val arrayList = CacheUtils.getFirstCity(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        CacheUtils.setCities(arrayList)
        getView()?.setCitySpinner(arrayList)
    }

    private fun processProfession(it: List<Profession>?) {
        val arrayList = CacheUtils.getFirstProfession(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        getView()?.setProfessionSpinner(arrayList)
    }

    private fun processSubProfession(it: List<Profession>?) {
        val arrayList = CacheUtils.getFirstSubProfession(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        getView()?.setSubProfessionSpinner(arrayList)
    }


    private fun processDistMaster(it: List<DistrictMaster>) {
        val arrayList = CacheUtils.getFirstDistrict(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        CacheUtils.setDistricts(arrayList)
        getView()?.setDistrictSpinner(arrayList)
    }


    override fun getKycIdTypes() {
        val selectedLangId = AppUtils.getSelectedLangId()
        disposables?.add(getKycIdTypesUseCase.getKycIdTypesByLang(selectedLangId).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processKycIdTypes(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )

    }

    private fun processKycIdTypes(it: List<KycIdTypes>?) {
        val arrayList = CacheUtils.getFirstKycTypes(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        getView()?.setKycTypesSpinner(arrayList)
    }


}
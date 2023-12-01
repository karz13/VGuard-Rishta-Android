package com.tfl.vguardrishta.ui.components.reupdateKyc

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
import io.paperdb.Paper
import javax.inject.Inject

class ReUpdateKycPresenter @Inject constructor(
    val kycUseCase: KycUpdateUseCase,
    val context: Context,
    val stateMasterUseCase: StateMasterUseCase,
    val districtMasterUseCase: DistrictMasterUseCase,
    val cityMasterUseCase: CityMasterUseCase,
    val getRishtaUserProfileUseCase: GetRishtaUserProfileUseCase,
    val bankTransferUseCase: BankTransferUseCase,
    val professionTypesUseCase: ProfessionTypesUseCase,
    val getKycIdTypesUseCase: GetKycIdTypesUseCase,
    val authenticateUserCaseCase: AuthenticateUserCaseCase

) : BasePresenter<ReUpdateKycContract.View>(), ReUpdateKycContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }


    override fun getDistrict(id: Long?) {
        disposables?.add(districtMasterUseCase.execute(id!!).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processCurrDistMaster(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun getStates() {
        disposables?.add(stateMasterUseCase.execute().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processCurrStateMasters(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )

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

    override fun getProfessions() {
        val rishtaUser = CacheUtils.getRishtaUser()
        disposables?.add(professionTypesUseCase.getProfessions(rishtaUser.professionId!!).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processProfession(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun processProfession(it: List<Profession>?) {
        val arrayList = CacheUtils.getFirstProfession(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        getView()?.setProfessionSpinner(arrayList)
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

    private fun processSubProfession(it: List<Profession>?) {
        val arrayList = CacheUtils.getFirstSubProfession(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        getView()?.setSubProfessionSpinner(arrayList)
    }


    private fun processBanks(it: List<BankDetail>?) {
        val arrayList = CacheUtils.getFirstBank(context)
        if (!it.isNullOrEmpty()) {
            arrayList.addAll(it)
        }
        CacheUtils.setBanks(arrayList)
        getView()?.showBanks(arrayList)
    }

    override fun getCities(id: Long?) {
        disposables?.add(cityMasterUseCase.execute(id!!).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processCurrCityMaster(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun processCurrStateMasters(it: List<StateMaster>?) {
        val arrayList = CacheUtils.getFirstState(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        CacheUtils.setStates(arrayList)
        getView()?.setCurrStatesSpinner(arrayList)
    }

    private fun processCurrDistMaster(it: List<DistrictMaster>) {
        val arrayList = CacheUtils.getFirstDistrict(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        CacheUtils.setDistricts(arrayList)
        getView()?.setCurrDistrictSpinner(arrayList)
    }

    private fun processCurrCityMaster(it: List<CityMaster>?) {
        val arrayList = CacheUtils.getFirstCity(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        CacheUtils.setCities(arrayList)
        getView()?.setCurrCitySpinner(arrayList)
    }

    private fun processPermStateMasters(it: List<StateMaster>?) {
        val arrayList = CacheUtils.getFirstState(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        getView()?.setPermStatesSpinner(arrayList)
    }

    private fun processPermDistMaster(it: List<DistrictMaster>) {
        val arrayList = CacheUtils.getFirstDistrict(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        getView()?.setPermDistrictSpinner(arrayList)
    }

    private fun processPermCityMaster(it: List<CityMaster>?) {
        val arrayList = CacheUtils.getFirstCity(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        getView()?.setPermCitySpinner(arrayList)
    }


    private fun processForCurrStateAndDist(it: List<CityMaster>) {
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
        processCurrStateMasters(stateList)
        processCurrDistMaster(distList)
    }


    private fun processForPermStateAndDist(it: List<CityMaster>) {
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
        processPermStateMasters(stateList)
        processPermDistMaster(distList)
    }

    private fun uploadPhotos(vru: VguardRishtaUser) {
        object : AsyncTask<Void, Void, Status>() {

            override fun onPreExecute() {
                getView()?.showProgress()
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): com.tfl.vguardrishta.models.Status? {

                val userPhoto = CacheUtils.getFileUploader().getUserPhotoFile()
                if (userPhoto != null) {
                    val entityUid = FileUtils.sendImage(userPhoto, Constants.PROFILE, "")
                    if (!entityUid.isNullOrEmpty()) {
                        vru.kycDetails.selfie = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }

                val checkBook = CacheUtils.getFileUploader().getChequeBookPhotoFile()
                if (checkBook != null) {
                    val entityUid = FileUtils.sendImage(checkBook, Constants.CHEQUE, "")
                    if (!entityUid.isNullOrEmpty()) {
                        vru.bankDetail?.checkPhoto = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }

                val idFileFront = CacheUtils.getFileUploader().getIdProofFileFront()
                if (idFileFront != null) {
                    val entityUid = FileUtils.sendImage(idFileFront, Constants.ID_CARD_FRONT, "")
                    if (!entityUid.isNullOrEmpty()) {
                        vru.kycDetails.aadharOrVoterOrDLFront = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }

                val idFileBack = CacheUtils.getFileUploader().getIdProofBackFile()
                if (idFileBack != null) {
                    val entityUid =
                        FileUtils.sendImage(idFileBack, Constants.ID_CARD_BACK, "")
                    if (!entityUid.isNullOrEmpty()) {
                        vru.kycDetails.aadharOrVoterOrDlBack = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }
                 //
                val pandCardFront = CacheUtils.getFileUploader().getPanCardFrontFile()
                if (pandCardFront != null) {
                    val entityUid =
                        FileUtils.sendImage(pandCardFront, Constants.PAN_CARD_FRONT, "")
                    if (!entityUid.isNullOrEmpty()) {
                        vru.kycDetails.panCardFront = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }
                val gstPic = CacheUtils.getFileUploader().getGstFile()
                if (gstPic != null) {
                    val entityUid =
                        FileUtils.sendImage(gstPic, Constants.GST, "")
                    if (!entityUid.isNullOrEmpty()) {
                        vru.gstPic = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }
//
//                val panCardBackFile = CacheUtils.getFileUploader().getPanCardBackFile()
//                if (panCardBackFile != null) {
//                    val entityUid = FileUtils.sendImage(
//                        panCardBackFile,
//                        Constants.PAN_CARD_BACK, ""!!
//                    )
//                    if (!entityUid.isNullOrEmpty()) {
//                        kyc.panCardBack = entityUid
//                    } else {
//                        return Status(400, "Failed")
//                    }
//                }
                return Status(200, "SUCCESS")
            }

            override fun onPostExecute(result: com.tfl.vguardrishta.models.Status?) {
                getView()?.stopProgress()
                if (result!!.code == 200) {
                    reUpdateUserKycDetails(vru)
                } else {
                    getView()?.showToast("File upload issue")
                }
            }
        }.execute()
    }

    override fun getKycIdTypes() {
        val selectedLangId = AppUtils.getSelectedLangId()
        disposables?.add(getKycIdTypesUseCase.getKycIdTypesByLang(selectedLangId).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                getView()?.processKycIdTypes(it)
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
                getView()?.setCurrCitiesByPinCode(it)
                processForCurrStateAndDist(it)
                processCurrCityMaster(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun getStateDistCitiesByPermPincodeId(pinCodeId: String) {
        disposables?.add(stateMasterUseCase.getStateDistCitiesByPincodeId(pinCodeId!!)
            .applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                getView()?.setPermCitiesByPinCode(it)
                processForPermStateAndDist(it)
                processPermCityMaster(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
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

    override fun getPermPincodeList(pinCode: String) {
        disposables?.add(stateMasterUseCase.getPincodeList(pinCode!!).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                getView()?.setPermPinCodeList(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun getUserDetails() {
        disposables?.add(authenticateUserCaseCase.getUser().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    saveUser(it, false)
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
        Paper.book().write(Constants.KEY_RISHTA_USER, user)
        getView()?.showUserDetails()
    }

    private fun reUpdateUserKycDetails(vru: VguardRishtaUser) {
        disposables?.add(getRishtaUserProfileUseCase.reUpdateUserForKyc(vru).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processResult(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun validate(rishtaUser: VguardRishtaUser) {

        if (rishtaUser.roleId == Constants.INF_USER_TYPE) {
            if (rishtaUser.genderPos.isNullOrEmpty()) {
                getView()?.showToast(context.getString(R.string.select_gender))
                return
            }
            if (rishtaUser.genderPos == "0") {
                getView()?.showToast(context.getString(R.string.select_gender))
                return
            }

            if (rishtaUser.dob.isNullOrEmpty()) {
                getView()?.showToast(context.getString(R.string.choose_dob))
                return
            }


            if (rishtaUser.isWhatsAppSame == null) {
                getView()?.showToast(context.getString(R.string.please_select_whats_app))
                return
            }

            if (!rishtaUser.whatsappNo.isNullOrEmpty() && !AppUtils.isValidMobileNo(rishtaUser.whatsappNo.toString())) {
                getView()?.showToast(context.getString(R.string.please_enter_valid_whatsapp))
                return
            }

            if (!rishtaUser.emailId.isNullOrEmpty() && !AppUtils.isValidEmailAddress(rishtaUser.emailId!!)) {
                getView()?.showToast(context.getString(R.string.enter_valid_mail))
                return
            }
        }


        if (rishtaUser.permanentAddress.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_perm_address))
            return
        }

        if (rishtaUser.streetAndLocality.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_street))
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

//            if (rishtaUser.currPinCodeId.isNullOrEmpty()) {
//                getView()?.showToast(context.getString(R.string.please_enter_pincode_to_get_state_and_dist))
//                return
//            }

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
        if (rishtaUser.roleId == Constants.INF_USER_TYPE) {
            if (rishtaUser.professionId == null || rishtaUser.professionId == -1) {
                getView()?.showToast(context.getString(R.string.select_profession))
                return
            }

            if (rishtaUser.maritalStatusId == null) {
                getView()?.showToast(context.getString(R.string.select_marital_status))
                return
            }


            val userPhoto = CacheUtils.getFileUploader().getUserPhotoFile()
            if (rishtaUser.kycDetails.selfie.isNullOrEmpty() && userPhoto == null) {
                getView()?.showToast(context.getString(R.string.please_click_your_selfie))
                return
            }

            /* if (rishtaUser.kycDetails.kycId == null || rishtaUser.kycDetails.kycId == -1) {
             getView()?.showToast(context.getString(R.string.select_idcard_type))
             return
         }*/
        }
        val idFileFront = CacheUtils.getFileUploader().getIdProofFileFront()
        if (rishtaUser.kycDetails.aadharOrVoterOrDLFront.isNullOrEmpty() && idFileFront == null) {
            getView()?.showToast(context.getString(R.string.upload_id_proof_front))
            return
        }

        val idFileBack = CacheUtils.getFileUploader().getIdProofBackFile()
        if (rishtaUser.kycDetails.aadharOrVoterOrDlBack.isNullOrEmpty() && idFileBack == null) {
            getView()?.showToast(context.getString(R.string.upload_id_proof_back))
            return
        }

        if (rishtaUser.kycDetails.aadharOrVoterOrDlNo.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_id_no_manually))
            return
        } else if (!AppUtils.isAadhaarValid(rishtaUser.kycDetails.aadharOrVoterOrDlNo!!)) {
            getView()?.showToast(context.getString(R.string.please_enter_valid_aadhar_no))
            return
        }

        val panFileFront = CacheUtils.getFileUploader().getPanCardFrontFile()
        if(rishtaUser.roleId == Constants.RET_USER_TYPE){
        if (rishtaUser.kycDetails.panCardFront.isNullOrEmpty() && panFileFront == null) {
            getView()?.showToast(context.getString(R.string.upload_pan_card_front))
            return
        }

        if (rishtaUser.kycDetails.panCardNo.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_pannumber))
            return
        }
        }

        if (!rishtaUser.kycDetails.panCardNo.isNullOrEmpty()) {
            if (!AppUtils.isValidPanNo(rishtaUser.kycDetails.panCardNo!!)){
                getView()?.showToast(context.getString(R.string.please_enter_valid_pan_no))
                return
            }
        }


        if (rishtaUser.roleId == Constants.INF_USER_TYPE) {
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

            if (checkBankDetails(rishtaUser)) {
                getView()?.showToast(context.getString(R.string.please_fill_your_complete_bank_details))
                return
            }


            val bankDetail = rishtaUser.bankDetail

            if (bankDetail != null) {

                if (!bankDetail.nomineeMobileNo.isNullOrEmpty() && !AppUtils.isValidMobileNo(
                        bankDetail.nomineeMobileNo!!
                    )
                ) {
                    getView()?.showToast(context.getString(R.string.enter_valid_mobileNo))
                    return
                }
                if (!bankDetail.nomineeEmail.isNullOrEmpty() && !AppUtils.isValidEmailAddress(
                        bankDetail.nomineeEmail!!
                    )
                ) {
                    getView()?.showToast(context.getString(R.string.enter_valid_mail))
                    return
                }
            }

            if (checkNomineeDetails(rishtaUser)) {
                getView()?.showToast(context.getString(R.string.please_fill_complete_nominee_details))
                return
            }
        }
        if (rishtaUser.roleId == Constants.RET_USER_TYPE) {
            val gstPhoto = CacheUtils.getFileUploader().getGstFile()
            if (!rishtaUser.gstYesNo.isNullOrEmpty() && rishtaUser.gstYesNo.equals("yess") && rishtaUser.gstPic.isNullOrEmpty() && gstPhoto == null) {
                getView()?.showToast(context.getString(R.string.upload_gst_photo))
                return
            }
            if (!rishtaUser.gstYesNo.isNullOrEmpty() && rishtaUser.gstYesNo.equals("yess") && rishtaUser.gstNo.isNullOrEmpty()) {
                getView()?.showToast(context.getString(R.string.please_enter_gst_no))
                return
            }
            if (!AppUtils.isValidGSTNo(rishtaUser.gstNo)) {
                getView()?.showToast(context.getString(R.string.valid_gst_no))
                return
            }
        }
        CacheUtils.saveVguardUser(rishtaUser)
        if (rishtaUser.roleId == Constants.INF_USER_TYPE)
            getView()?.navgateToPreview()
        else {
            getView()?.navigateToRetailerPreview()
        }
    }

    override fun registerUser() {
        val rishtaUser = CacheUtils.getRishtaUser()
        uploadPhotos(rishtaUser)
    }

    private fun checkNomineeDetails(rishtaUser: VguardRishtaUser): Boolean {
        val bankDetail = rishtaUser.bankDetail
        if (bankDetail?.nomineeName.isNullOrEmpty() || bankDetail?.nomineeDob.isNullOrEmpty()
            || bankDetail?.nomineeMobileNo.isNullOrEmpty() /*|| bankDetail?.nomineeEmail.isNullOrEmpty()*/
            || bankDetail?.nomineeAdd.isNullOrEmpty() || bankDetail?.nomineeRelation.isNullOrEmpty()
        ) {
            return true
        }
        return false
    }
/*    private fun checkNomineeDetails(rishtaUser: VguardRishtaUser): Boolean {
        val bankDetail = rishtaUser.bankDetail

        if (!bankDetail?.nomineeName.isNullOrEmpty() || !bankDetail?.nomineeDob.isNullOrEmpty()
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
        }
        return false
    }*/

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

    private fun processResult(it: Status?) {
        if (it == null) {
            getView()?.showToast(context.getString(R.string.problem_occurred))
            return
        }
        if (it.code == 200) {
            getView()?.showMsgDialog(it.message!!)
        } else if (it.code == 400) {
            getView()?.showToast(it.message!!)
        }
    }
}
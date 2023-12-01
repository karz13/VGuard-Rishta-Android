package com.tfl.vguardrishta.ui.components.vguard.updateProfile

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

class UpdateProfilePresenter @Inject constructor(
    val kycUseCase: KycUpdateUseCase,
    val context: Context,
    val stateMasterUseCase: StateMasterUseCase,
    val districtMasterUseCase: DistrictMasterUseCase,
    val cityMasterUseCase: CityMasterUseCase,
    val getRishtaUserProfileUseCase: GetRishtaUserProfileUseCase,
    val bankTransferUseCase: BankTransferUseCase,
    val professionTypesUseCase: ProfessionTypesUseCase,
    val getKycIdTypesUseCase: GetKycIdTypesUseCase

) : BasePresenter<UpdateProfileContract.View>(), UpdateProfileContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
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
                processCityMaster(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
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


    private fun processDistMaster(it: List<DistrictMaster>) {
        val arrayList = CacheUtils.getFirstDistrict(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        CacheUtils.setDistricts(arrayList)
        getView()?.setDistrictSpinner(arrayList)
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

//                val idFileFront = CacheUtils.getFileUploader().getIdProofFileFront()
//                if (idFileFront != null) {
//                    val entityUid = FileUtils.sendImage(idFileFront, Constants.ID_CARD_FRONT, "")
//                    if (!entityUid.isNullOrEmpty()) {
//                        kyc.aadharOrVoterOrDLFront = entityUid
//                    } else {
//                        return Status(400, "Failed")
//                    }
//                }
//
//                val idFileBack = CacheUtils.getFileUploader().getIdProofBackFile()
//                if (idFileBack != null) {
//                    val entityUid =
//                        FileUtils.sendImage(idFileBack, Constants.ID_CARD_BACK, "")
//                    if (!entityUid.isNullOrEmpty()) {
//                        kyc.aadharOrVoterOrDlBack = entityUid
//                    } else {
//                        return Status(400, "Failed")
//                    }
//                }
//
//                val pandCardFront = CacheUtils.getFileUploader().getPanCardFrontFile()
//                if (pandCardFront != null) {
//                    val entityUid =
//                        FileUtils.sendImage(pandCardFront, Constants.PAN_CARD_FRONT, "")
//                    if (!entityUid.isNullOrEmpty()) {
//                        kyc.panCardFront = entityUid
//                    } else {
//                        return Status(400, "Failed")
//                    }
//                }
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
                    updateUserDetails(vru)
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

    private fun updateUserDetails(vru: VguardRishtaUser) {
        disposables?.add(getRishtaUserProfileUseCase.updateProfile(vru).applySchedulers()
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

        if (rishtaUser.genderPos.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.select_gender))
            return
        }
        if (rishtaUser.genderPos == "0") {
            getView()?.showToast(context.getString(R.string.select_gender))
            return
        }

        if (rishtaUser.addDiff == -1) {
            getView()?.showToast(context.getString(R.string.select_is_current_address_different))
            return
        }

        if (!rishtaUser.currPinCode.isNullOrEmpty() && !AppUtils.isValidPinCode(rishtaUser.currPinCode!!)) {
            getView()?.showToast(context.getString(R.string.enter_valid_pincode))
            return
        }

        if (!rishtaUser.emailId.isNullOrEmpty() && !AppUtils.isValidEmailAddress(rishtaUser.emailId!!)) {
            getView()?.showToast(context.getString(R.string.enter_valid_mail))
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

      /*  if (checkBankDetails(rishtaUser)) {
            getView()?.showToast(context.getString(R.string.please_fill_your_complete_bank_details))
            return
        }*/

        /*if (rishtaUser.bankDetail!!.bankIfsc!!.length!=11) {
            getView()?.showToast(context.getString(R.string.please_enter_valid_ifcsc2))
            return
        }*/

        val bankDetail = rishtaUser.bankDetail

        if (bankDetail != null) {

            if (!bankDetail.nomineeMobileNo.isNullOrEmpty() && !AppUtils.isValidMobileNo(bankDetail.nomineeMobileNo!!)) {
                getView()?.showToast(context.getString(R.string.enter_valid_mobileNo))
                return
            }
            if (!bankDetail.nomineeEmail.isNullOrEmpty() && !AppUtils.isValidEmailAddress(bankDetail.nomineeEmail!!)) {
                getView()?.showToast(context.getString(R.string.enter_valid_mail))
                return
            }
        }

        if (checkNomineeDetails(rishtaUser)) {
            getView()?.showToast(context.getString(R.string.please_fill_complete_nominee_details))
            return
        }

        uploadPhotos(rishtaUser)
    }

    private fun checkNomineeDetails(rishtaUser: VguardRishtaUser): Boolean {
        val bankDetail = rishtaUser.bankDetail
        if (bankDetail?.nomineeName.isNullOrEmpty() || bankDetail?.nomineeDob.isNullOrEmpty()
            || bankDetail?.nomineeMobileNo.isNullOrEmpty()  /*|| bankDetail?.nomineeEmail.isNullOrEmpty()*/
            || bankDetail?.nomineeAdd.isNullOrEmpty() || bankDetail?.nomineeRelation.isNullOrEmpty()
        ) {
            return true
        }
        return false
    }
    /*   private fun checkNomineeDetails(rishtaUser: VguardRishtaUser): Boolean {
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
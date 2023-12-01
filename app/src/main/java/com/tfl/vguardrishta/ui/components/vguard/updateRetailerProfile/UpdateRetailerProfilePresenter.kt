package com.tfl.vguardrishta.ui.components.vguard.updateRetailerProfile

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

class UpdateRetailerProfilePresenter @Inject constructor(
    val context: Context,
    val authenticateUserCaseCase: AuthenticateUserCaseCase,
    val kycUseCase: KycUpdateUseCase,
    val stateMasterUseCase: StateMasterUseCase,
    val districtMasterUseCase: DistrictMasterUseCase,
    val cityMasterUseCase: CityMasterUseCase,
    val getRishtaUserProfileUseCase: GetRishtaUserProfileUseCase,
    val bankTransferUseCase: BankTransferUseCase,
    val professionTypesUseCase: ProfessionTypesUseCase,
    val getKycIdTypesUseCase: GetKycIdTypesUseCase,
    val productUseCase: RegisterProductUseCase

) : BasePresenter<UpdateRetailerProfileContract.View>(), UpdateRetailerProfileContract.Presenter {


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getProfile() {
        disposables?.add(authenticateUserCaseCase.getUser().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    getView()?.showRishtaUser(it)
                }
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


    override fun getPermStateDistCitiesByPincodeId(pinCodeId: String) {
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

       /* if (checkBankDetails(rishtaUser)) {
            getView()?.showToast(context.getString(R.string.please_fill_your_complete_bank_details))
            return
        }*/
        uploadPhotos(rishtaUser)
    }

    override fun getRetailerCategoryDealIn() {
        disposables?.add(productUseCase.getRetailerCategoryDealIn()
            .applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                val firstRetailerCategoryDealIn = CacheUtils.getFirstRetailerCategoryDealIn(context)
                firstRetailerCategoryDealIn.addAll(it)
                getView()?.setRetailerCategoryDealIn(firstRetailerCategoryDealIn)
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

    private fun checkBankDetails(rishtaUser: VguardRishtaUser): Boolean {
        val bankDetail = rishtaUser.bankDetail
        var isCheckPhotoAvailable = checkBankCheckAvaialbe(rishtaUser)

        if (!bankDetail?.bankNameAndBranch.isNullOrEmpty()
            || !bankDetail?.bankAccHolderName.isNullOrEmpty() || !bankDetail?.bankAccNo.isNullOrEmpty()
            || !bankDetail?.bankIfsc.isNullOrEmpty() || isCheckPhotoAvailable
        ) {
            if (!bankDetail?.bankNameAndBranch.isNullOrEmpty()
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

    private fun processPermStateMasters(it: List<StateMaster>?) {
        val arrayList = CacheUtils.getFirstState(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        getView()?.setPermStatesSpinner(arrayList)
    }

    private fun processPermCityMaster(it: List<CityMaster>?) {
        val arrayList = CacheUtils.getFirstCity(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        getView()?.setPermCitySpinner(arrayList)
    }

    private fun processDistMaster(it: List<DistrictMaster>) {
        val arrayList = CacheUtils.getFirstDistrict(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        CacheUtils.setDistricts(arrayList)
        getView()?.setDistrictSpinner(arrayList)
    }

    private fun processPermDistMaster(it: List<DistrictMaster>) {
        val arrayList = CacheUtils.getFirstDistrict(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        getView()?.setPermDistrictSpinner(arrayList)
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

}
package com.tfl.vguardrishta.ui.components.vguard.fragment.retailerProfile

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

class RetailerUserProfilePresenter @Inject constructor(
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

) : BasePresenter<RetailerUserProfileContract.View>(), RetailerUserProfileContract.Presenter {


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
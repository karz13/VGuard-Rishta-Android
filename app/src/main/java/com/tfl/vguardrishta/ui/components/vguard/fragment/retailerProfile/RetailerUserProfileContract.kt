package com.tfl.vguardrishta.ui.components.vguard.fragment.retailerProfile

import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.ui.base.BaseContract
import java.util.*

interface RetailerUserProfileContract {
    interface View : BaseContract.View {

        fun initUI()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun showRishtaUser(it: VguardRishtaUser)
        fun setStatesSpinner(arrayList: ArrayList<StateMaster>)
        fun setDistrictSpinner(arrayList: ArrayList<DistrictMaster>)
        fun setCitySpinner(arrayList: ArrayList<CityMaster>)
        fun showBanks(arrayList: ArrayList<BankDetail>)
        fun setPinCodeList(it: List<PincodeDetails>)
        fun setCitiesByPinCode(it: List<CityMaster>?)
        fun setPermCitiesByPinCode(it: List<CityMaster>?)
        fun setPermStatesSpinner(arrayList: ArrayList<StateMaster>)
        fun setPermCitySpinner(arrayList: ArrayList<CityMaster>)
        fun setPermDistrictSpinner(arrayList: ArrayList<DistrictMaster>)
        fun setPermPinCodeList(it: List<PincodeDetails>)
        fun showMsgDialog(message: String)
        fun setRetailerCategoryDealIn(it: List<Category>)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getProfile()

        fun getCities(id: Long?)
        fun getDistrict(id: Long?)
        fun getStates()
        fun getBanks()
        fun getStateDistCitiesByPincodeId(pinCode: String)
        fun getPincodeList(toString: String)
        fun getPermStateDistCitiesByPincodeId(pincode: String)
        fun getPermPincodeList(toString: String)
         fun getRetailerCategoryDealIn()
    }

}
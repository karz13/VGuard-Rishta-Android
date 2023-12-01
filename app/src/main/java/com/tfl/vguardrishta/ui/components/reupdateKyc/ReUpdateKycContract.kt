package com.tfl.vguardrishta.ui.components.reupdateKyc

import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.ui.base.BaseContract
import java.util.ArrayList

interface ReUpdateKycContract {

    interface View : BaseContract.View {

        fun initUI()

        fun hideKeyBoard()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun setCurrStatesSpinner(arrayList: ArrayList<StateMaster>){}
        fun setCurrDistrictSpinner(arrayList: ArrayList<DistrictMaster>){}
        fun setCurrCitySpinner(arrayList: ArrayList<CityMaster>){}
        fun showBanks(arrayList: ArrayList<BankDetail>){}
        fun setProfessionSpinner(arrayList: ArrayList<Profession>){}
        fun setSubProfessionSpinner(arrayList: ArrayList<Profession>){}
        fun showMsgDialog(message: String){}
        fun processKycIdTypes(it: List<KycIdTypes>){}
        fun setPinCodeList(it: List<PincodeDetails>){}
        fun setCurrCitiesByPinCode(it: List<CityMaster>?){}
        fun showUserDetails(){}
        fun setPermCitiesByPinCode(it: List<CityMaster>?){}
        fun setPermStatesSpinner(arrayList: ArrayList<StateMaster>){}
        fun setPermDistrictSpinner(arrayList: ArrayList<DistrictMaster>){}
        fun setPermCitySpinner(arrayList: ArrayList<CityMaster>){}
        fun setPermPinCodeList(it: List<PincodeDetails>){}
        fun navigateToLogin(){}
        fun navgateToPreview(){}
        fun navigateToRetailerPreview(){}
    }

    interface Presenter : BaseContract.Presenter<View> {
         fun getCities(id: Long?)
        fun getDistrict(id: Long?)
        fun getStates()
        fun validate(rishtaUser: VguardRishtaUser)
        fun getBanks()
        fun getProfessions()
        fun getSubProfessions(professionId: String)
        fun getKycIdTypes()
        fun getStateDistCitiesByPincodeId(pinCode: String)
        fun getPincodeList(toString: String)
        fun getUserDetails()
        fun getStateDistCitiesByPermPincodeId(pinCode: String)
        fun getPermPincodeList(toString: String)
        fun registerUser()
    }

}
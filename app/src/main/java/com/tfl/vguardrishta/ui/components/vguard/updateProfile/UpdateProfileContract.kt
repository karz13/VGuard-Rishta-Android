package com.tfl.vguardrishta.ui.components.vguard.updateProfile

import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.ui.base.BaseContract
import java.util.ArrayList

interface UpdateProfileContract {

    interface View : BaseContract.View {

        fun initUI()

        fun hideKeyBoard()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun setStatesSpinner(arrayList: ArrayList<StateMaster>)
        fun setDistrictSpinner(arrayList: ArrayList<DistrictMaster>)
        fun setCitySpinner(arrayList: ArrayList<CityMaster>)
        fun showBanks(arrayList: ArrayList<BankDetail>)
        fun setProfessionSpinner(arrayList: ArrayList<Profession>)
        fun setSubProfessionSpinner(arrayList: ArrayList<Profession>)
        fun showMsgDialog(message: String)
        fun processKycIdTypes(it: List<KycIdTypes>?)
        fun setPinCodeList(it: List<PincodeDetails>)
        fun setCitiesByPinCode(it: List<CityMaster>?)
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
    }

}
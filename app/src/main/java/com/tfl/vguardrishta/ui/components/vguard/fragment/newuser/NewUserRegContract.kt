package com.tfl.vguardrishta.ui.components.vguard.fragment.newuser

import com.tfl.vguardrishta.models.Profession
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.ui.base.BaseContract
import com.tfl.vguardrishta.models.BankDetail
import com.tfl.vguardrishta.models.VguardRishtaUser
import java.util.ArrayList

interface NewUserRegContract {

    interface View : BaseContract.View {

        fun initUI()

        fun showProgress()

        fun stopProgress()

        fun showToast(toast: String)

        fun showError()
        fun hideKeyBoard(){}


        fun navigateToOtp(){}
        fun navigateToBankAndNomineeDetails(){}
        fun setStatesSpinner(arrayList: ArrayList<StateMaster>){}
        fun showUserDetailsPreview(){}
        fun navigateToLogin(){}
        fun navigateToNewUserKyc(){}
        fun setKycTypesSpinner(arrayList: ArrayList<KycIdTypes>){}
        fun setDistrictSpinner(arrayList: ArrayList<DistrictMaster>){}
        fun setCitySpinner(arrayList: ArrayList<CityMaster>){}
        fun showMsgDialogAndFinish(message: String){}
        fun showNameOfReferee(nameOfReferee: String){}
        fun showBanks(arrayList: ArrayList<BankDetail>){}
        fun setProfessionSpinner(arrayList: ArrayList<Profession>){}
        fun setSubProfessionSpinner(arrayList: ArrayList<Profession>){}
        fun setDetailsByPinCode(it: PincodeDetails?){}
        fun setPinCodeList(it: List<PincodeDetails>){}
        fun setCitiesByPinCode(it: List<CityMaster>?){}
        fun showErrorDialog(message: String){}
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun generateOtpForNewUser(vguardRishtaUser: VguardRishtaUser)
        fun getStates()
        fun verifyDetails(rishtaUser: VguardRishtaUser)
        fun registerUser()
        fun verifyNewUserKycData(rishtaUser: VguardRishtaUser)
        fun getKycIdTypes()
        fun getDistrict(id: Long?)
        fun getCities(id: Long?)
        fun getReferalName(code: String)


        fun getBanks()
        fun getProfessions()
        fun getSubProfessions(professionId: String)
        fun getStateDistByPinCode(pinCode: String)
         fun getPincodeList(pinCode: String)
        fun getStateDistCitiesByPincodeId(pinCode: String)
    }

}
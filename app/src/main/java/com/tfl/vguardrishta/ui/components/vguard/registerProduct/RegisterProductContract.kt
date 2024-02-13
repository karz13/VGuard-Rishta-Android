package com.tfl.vguardrishta.ui.components.vguard.registerProduct

import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.ui.base.BaseContract
import java.util.*

interface RegisterProductContract {

    interface View : BaseContract.View {

        fun initUI()
        fun showProgress()
        fun stopProgress()
        fun showToast(toast: String)
        fun showError()
        fun showNoData() {}
        fun setProductsCatalog(list: List<ProductDetail>) {}
        fun setCustDetails(it: CustomerDetailsRegistration) {}
        fun showErrorDialogWithFinish(errorMsg: String) {}
        fun showErrorDialog(errorMsg: String?) {}
        fun showCouponPoints(errorMsg: CouponResponse) {}
        fun setStatesSpinner(arrayList: ArrayList<StateMaster>) {}
        fun setDistrictSpinner(arrayList: ArrayList<DistrictMaster>) {}
        fun setDealerDistrictSpinner(arrayList: ArrayList<DistrictMaster>) {}
        fun setStateDistrictAndCity(it: Triple<String, String,String>, isDealer: Boolean){}
        fun setPinCodeList(it: List<PincodeDetails>, isDealer: Boolean){}
        fun processValidateMobile(validation: MobileValidation){}
        fun proceedToNextPage()

    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getRetProductCategories()
        fun productOTP(otp:OTP)
        fun getCustDetByMobile(mobileNo: String)
        fun validateMobile(mobileNo: String, dealerCategory: String)
        fun sendCustomerData(cdr: CustomerDetailsRegistration, isSingleScan:Boolean)
        fun getStatesFromCrmApi()
        fun getDistrictsFromCrmApi(stateName: String, s: String)
        fun getCRMStateDistrictByPincode(pinCode: String, isDealer:Boolean)
        fun getPincodeList(pinCode: String, isDealer: Boolean)
    }

}
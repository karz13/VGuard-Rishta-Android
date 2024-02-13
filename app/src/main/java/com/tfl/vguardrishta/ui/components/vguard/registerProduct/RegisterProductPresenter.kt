package com.tfl.vguardrishta.ui.components.vguard.registerProduct

import android.content.Context
import android.os.AsyncTask
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.DistrictMasterUseCase
import com.tfl.vguardrishta.domain.RegisterProductUseCase
import com.tfl.vguardrishta.domain.StateMasterUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.CustomerDetailsRegistration
import com.tfl.vguardrishta.models.DistrictMaster
import com.tfl.vguardrishta.models.OTP
import com.tfl.vguardrishta.models.StateMaster
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.FileUtils
import javax.inject.Inject

class RegisterProductPresenter @Inject constructor(
    private val context: Context,
    private val registerProductUseCase: RegisterProductUseCase,
    private val stateMasterUseCase: StateMasterUseCase,
    private val districtMasterUseCase: DistrictMasterUseCase
) : BasePresenter<RegisterProductContract.View>(),
    RegisterProductContract.Presenter {


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getRetProductCategories() {
        disposables?.add(registerProductUseCase.execute(Unit).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                getView()?.setProductsCatalog(it)
            }, {
                getView()?.showError()
            })
        )
    }

    override fun productOTP(otp: OTP){
        disposables?.add(registerProductUseCase.productgenerateOTP(otp).applySchedulers()
            .doOnSubscribe{getView()?.showProgress()}
            .doFinally{getView()?.stopProgress()}
            .subscribe({

                if(it?.code==0){
                    getView()?.proceedToNextPage()
                }else{
                    getView()?.showErrorDialog(it.message)
                }
            }, {

                getView()?.showError()
            }))
    }

    override fun sendCustomerData(cdr: CustomerDetailsRegistration, isSingleScan: Boolean) {
        if (isSingleScan && cdr.contactNo.isNullOrEmpty()) {
            getView()?.showErrorDialog(context.getString(R.string.enter_mandatory_fields))
        }
        val billDetailFile = CacheUtils.getFileUploader().getBillDetailsFile()
        if(billDetailFile==null && cdr.cresp.anomaly==1 && cdr.dealerCategory!="Sub-Dealer")
        {
            getView()?.showErrorDialog(context.getString(R.string.enter_mandatory_fields))
            return
        }
        uploadPhotos(cdr, isSingleScan)
    }

    override fun getStatesFromCrmApi() {
        disposables?.add(stateMasterUseCase.getStatesFromCrmApi().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processStateMasters(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun getDistrictsFromCrmApi(stateName: String, typeCustomerOrDealer: String) {

        disposables?.add(districtMasterUseCase.getDistrictsFromCrmApi(stateName).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processDistrictMasters(it, typeCustomerOrDealer)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun getCRMStateDistrictByPincode(pinCode: String, isDealer: Boolean) {
        disposables?.add(stateMasterUseCase.getCRMStateDistrictByPincode(pinCode).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
              getView()?.setStateDistrictAndCity(it, isDealer)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun getPincodeList(pinCode: String, isDealer: Boolean) {
        disposables?.add(stateMasterUseCase.getCRMPinCodeList(pinCode).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (!it.isNullOrEmpty())
                    getView()?.setPinCodeList(it, isDealer)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    private fun processDistrictMasters(it: List<DistrictMaster>, typeCustomerOrDealer: String) {
        val arrayList = CacheUtils.getFirstDistrict(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        if (typeCustomerOrDealer == "custState") {
            getView()?.setDistrictSpinner(arrayList)
        } else {
            getView()?.setDealerDistrictSpinner(arrayList)
        }


    }

    private fun processStateMasters(it: List<StateMaster>?) {
        val arrayList = CacheUtils.getFirstState(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        getView()?.setStatesSpinner(arrayList)
    }

    private fun uploadPhotos(ru: CustomerDetailsRegistration, isSingleScan: Boolean) {
        object : AsyncTask<Void, Void, Status>() {

            override fun onPreExecute() {
                getView()?.showProgress()
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): com.tfl.vguardrishta.models.Status? {

                val warrantyFile = CacheUtils.getFileUploader().getWarrantyFile()
                if (warrantyFile != null) {
                    val entityUid = FileUtils.sendImage(warrantyFile, Constants.WARRANTY, "")
                    if (!entityUid.isNullOrEmpty()) {
                        ru.warrantyPhoto = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }
                val billDetailFile = CacheUtils.getFileUploader().getBillDetailsFile()
                if (billDetailFile != null) {
                    val entityUid = FileUtils.sendImage(billDetailFile, Constants.BILL, "")
                    if (!entityUid.isNullOrEmpty()) {
                        ru.billDetails = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }
                return Status(200, "SUCCESS")

            }

            override fun onPostExecute(result: com.tfl.vguardrishta.models.Status?) {
                getView()?.stopProgress()
                if (result!!.code == 200) {
                    sendCustomerDataAfterUpload(ru, isSingleScan)
                } else {
                    getView()?.showToast(context.getString(R.string.file_upload_issue))
                }
            }
        }.execute()
    }

    private fun sendCustomerDataAfterUpload(cdr: CustomerDetailsRegistration, isSingleScan: Boolean) {
        disposables?.add(registerProductUseCase.registerWarranty(cdr).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it?.errorCode == 1) {
                    getView()?.showCouponPoints(it)
                } else {
                    getView()?.showErrorDialog(it.errorMsg)
                }

            }, {
                getView()?.showError()
            })
        )
    }


    override fun getCustDetByMobile(mobileNo: String) {
        disposables?.add(registerProductUseCase.getCustDetByMobile(mobileNo).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it?.name != null && it.name!!.isNotEmpty()) {
                    getView()?.setCustDetails(it)
                } else {
                    getView()?.showToast(context.getString(R.string.cust_detail_not_found))
                }
            }, {
                getView()?.showError()
            })
        )
    }

    override fun validateMobile(mobileNo: String, dealerCategory: String) {
        disposables?.add(registerProductUseCase.validateMobile(mobileNo,dealerCategory).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                    getView()?.processValidateMobile(it)
            }, {
                getView()?.showError()
            })
        )
    }
}
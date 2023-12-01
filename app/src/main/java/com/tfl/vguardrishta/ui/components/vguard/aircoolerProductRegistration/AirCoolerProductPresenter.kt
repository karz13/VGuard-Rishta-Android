package com.tfl.vguardrishta.ui.components.vguard.aircoolerProductRegistration

import android.content.Context
import android.os.AsyncTask
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.RegisterProductUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.CustomerDetailsRegistration
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.FileUtils
import javax.inject.Inject

class AirCoolerProductPresenter @Inject constructor(
    private val registerProductUseCase: RegisterProductUseCase,
    private val context: Context
) : BasePresenter<AirCoolerProductContract.View>(),
    AirCoolerProductContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun sendDataToServer(cdr: CustomerDetailsRegistration) {
        uploadPhotos(cdr)
    }

    private fun uploadPhotos(ru: CustomerDetailsRegistration) {
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
                    sendCustomerDataAfterUpload(ru)
                } else {
                    getView()?.showToast(context.getString(R.string.file_upload_issue))
                }
            }
        }.execute()
    }

    private fun sendCustomerDataAfterUpload(cdr: CustomerDetailsRegistration) {
        disposables?.add(registerProductUseCase.senAirCoolerData(cdr).applySchedulers()
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

}
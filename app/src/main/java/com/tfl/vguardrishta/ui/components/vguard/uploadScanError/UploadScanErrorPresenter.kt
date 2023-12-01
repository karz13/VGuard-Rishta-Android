package com.tfl.vguardrishta.ui.components.vguard.uploadScanError

import android.content.Context
import android.os.AsyncTask
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.ProcessErrorCouponUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.ErrorCoupon
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.FileUtils
import javax.inject.Inject

class UploadScanErrorPresenter @Inject constructor(
    val context: Context,
    val processErrorCouponUseCase: ProcessErrorCouponUseCase


) : BasePresenter<UploadScanErrorContract.View>(), UploadScanErrorContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun uploadScanError(remarks: String, barCode: String) {

        val errorCouponFile = CacheUtils.getFileUploader().getErrorCouponFile()
        if (errorCouponFile == null) {
            getView()?.showToast(context.getString(R.string.please_capture_or_upload_invalid_coupon_picture))
            return
        }

        if (barCode.isEmpty()) {
            getView()?.showToast(context.getString(R.string.error_barcode))
            return
        }
        if(barCode.length<16){
            getView()?.showToast(context.getString(R.string.please_enter_valid_sixteen_digit))
            return
        }

        if (remarks.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.enter_remarks))
            return
        }





        val errorCoupon = ErrorCoupon()
        errorCoupon.remarks = remarks
        errorCoupon.couponCode=barCode
        uploadPhotos(errorCoupon)
    }

    private fun uploadPhotos(ec: ErrorCoupon) {
        object : AsyncTask<Void, Void, Status>() {

            override fun onPreExecute() {
                getView()?.showProgress()
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): com.tfl.vguardrishta.models.Status? {
                val errorCouponFile = CacheUtils.getFileUploader().getErrorCouponFile()
                if (errorCouponFile != null) {
                    val entityUid = FileUtils.sendImage(errorCouponFile, Constants.ERROR_COUPON, "")
                    if (!entityUid.isNullOrEmpty()) {
                        ec.errorCouponPath = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }
                return Status(200, "SUCCESS")
            }

            override fun onPostExecute(result: com.tfl.vguardrishta.models.Status?) {
                getView()?.stopProgress()
                if (result!!.code == 200) {
                    sendErrorCoupon(ec)
                } else {
                    getView()?.showToast("File upload issue")
                }
            }
        }.execute()
    }

    private fun sendErrorCoupon(ec: ErrorCoupon) {
        disposables?.add(processErrorCouponUseCase.execute(ec).applySchedulers()
            .doOnSubscribe { }
            .doFinally { }
            .subscribe({
                processResult(it)
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
            getView()?.showMsgDialogWithFinish(it.message!!)
        } else if (it.code == 400) {
            getView()?.showToast(it.message!!)
        }
    }
}
package com.tfl.vguardrishta.ui.components.vguard.updateKycRetailer

import android.content.Context
import android.os.AsyncTask
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetKycIdTypesUseCase
import com.tfl.vguardrishta.domain.KycUpdateUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.KycDetails
import com.tfl.vguardrishta.models.KycRetailerDetails
import com.tfl.vguardrishta.models.KycIdTypes
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.ui.components.vguard.updateKycRetailer.UpdateKycRetailerContract
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.FileUtils
import javax.inject.Inject

class UpdateKycRetailerPresenter @Inject constructor(
    val kycUseCase: KycUpdateUseCase,
    val context: Context,
    val getKycIdTypesUseCase: GetKycIdTypesUseCase

) : BasePresenter<UpdateKycRetailerContract.View>(), UpdateKycRetailerContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun uploadKycDetails(
        kycDetails: KycRetailerDetails
    ) {

  /*      if (kycDetails.kycId == -1) {
            getView()?.showToast(context.getString(R.string.select_idcard_type))
            return
        }

        if (kycDetails.selfie.isNullOrEmpty()) {
            val selfie = CacheUtils.getFileUploader().getUserPhotoFile()
            if (selfie == null) {
                getView()?.showToast(context.getString(R.string.please_click_your_selfie))
                return
            }
        }

        if(kycDetails.aadharOrVoterOrDLFront.isNullOrEmpty()){
            val idFileFront = CacheUtils.getFileUploader().getIdProofFileFront()
            if (idFileFront == null) {
                getView()?.showToast(context.getString(R.string.upload_id_proof_front))
                return
            }
        }

        if(kycDetails.aadharOrVoterOrDlBack.isNullOrEmpty()){
            val idFileBack = CacheUtils.getFileUploader().getIdProofBackFile()
            if (idFileBack == null) {
                getView()?.showToast(context.getString(R.string.upload_id_proof_back))
                return
            }
        }
        if(kycDetails.aadharOrVoterOrDlNo.isNullOrEmpty()){
            getView()?.showToast(context.getString(R.string.please_enter_id_no_manually))
            return
        }
        else if (!AppUtils.isAadhaarValid(kycDetails.aadharOrVoterOrDlNo!!)){
            getView()?.showToast(context.getString(R.string.please_enter_valid_aadhar_no))
            return
        }*/

       /* if(kycDetails.panCardFront.isNullOrEmpty()){
            val panFileFront = CacheUtils.getFileUploader().getPanCardFrontFile()
            if (panFileFront == null) {
                getView()?.showToast(context.getString(R.string.upload_pan_card_front))
                return
            }
        }
        if (kycDetails.panCardNo.isNullOrEmpty()) {

            getView()?.showToast(context.getString(R.string.please_enter_pannumber))
            return

        }*/

        if (!kycDetails.panCardNo.isNullOrEmpty()) {

            if (!AppUtils.isValidPanNo(kycDetails.panCardNo)) {
                getView()?.showToast(context.getString(R.string.please_enter_valid_pan_no))
                return

            }

        }



        uploadPhotos(kycDetails)
    }

    override fun getKycIdTypes() {
        val selectedLangId = AppUtils.getSelectedLangId()
        disposables?.add(getKycIdTypesUseCase.getKycIdTypesByLang(selectedLangId).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processKycIdTypes(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )

    }

    private fun processKycIdTypes(it: List<KycIdTypes>?) {
        val arrayList = CacheUtils.getFirstKycTypes(context)
        if (!it.isNullOrEmpty()) arrayList.addAll(it)
        getView()?.setKycTypesSpinner(arrayList)
    }

    override fun getKycDetails() {
        disposables?.add(getKycIdTypesUseCase.getKycDetails().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
               processResult2(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )

    }


    private fun uploadPhotos(kyc: KycRetailerDetails) {
        object : AsyncTask<Void, Void, Status>() {

            override fun onPreExecute() {
                getView()?.showProgress()
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): com.tfl.vguardrishta.models.Status? {

                val userPhoto = CacheUtils.getFileUploader().getUserPhotoFile()
               /* if (userPhoto != null) {
                    val entityUid = FileUtils.sendImage(userPhoto, Constants.PROFILE, "")
                    if (!entityUid.isNullOrEmpty()) {
                        kyc.selfie = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }

                val idFileFront = CacheUtils.getFileUploader().getIdProofFileFront()
                if (idFileFront != null) {
                    val entityUid = FileUtils.sendImage(idFileFront, Constants.ID_CARD_FRONT, "")
                    if (!entityUid.isNullOrEmpty()) {
                        kyc.aadharOrVoterOrDLFront = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }

                val idFileBack = CacheUtils.getFileUploader().getIdProofBackFile()
                if (idFileBack != null) {
                    val entityUid =
                        FileUtils.sendImage(idFileBack, Constants.ID_CARD_BACK, "")
                    if (!entityUid.isNullOrEmpty()) {
                        kyc.aadharOrVoterOrDlBack = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }
*/
                val pandCardFront = CacheUtils.getFileUploader().getPanCardFrontFile()
                if (pandCardFront != null) {
                    val entityUid =
                        FileUtils.sendImage(pandCardFront, Constants.PAN_CARD_FRONT, "")
                    if (!entityUid.isNullOrEmpty()) {
                        kyc.panCardFront = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }

               /* val panCardBackFile = CacheUtils.getFileUploader().getPanCardBackFile()
                if (panCardBackFile != null) {
                    val entityUid = FileUtils.sendImage(
                        panCardBackFile,
                        Constants.PAN_CARD_BACK, ""!!
                    )
                    if (!entityUid.isNullOrEmpty()) {
                        kyc.panCardBack = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }*/
                return Status(200, "SUCCESS")
            }

            override fun onPostExecute(result: com.tfl.vguardrishta.models.Status?) {
                getView()?.stopProgress()
                if (result!!.code == 200) {
                    sendKycDetails(kyc)
                } else {
                    getView()?.showMsgDialog("File upload issue")
                }
            }
        }.execute()

    }

    private fun sendKycDetails(kyc: KycRetailerDetails) {
       disposables?.add(kycUseCase.executeForRetailer(kyc).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
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
            getView()?.showMsgDialog(it.message!!)
        } else if (it.code == 400) {
            getView()?.showMsgDialog(it.message!!)
        }
    }

    private fun processResult2(it: KycDetails) {
        getView()?.showKycDetails(it)
    }
}
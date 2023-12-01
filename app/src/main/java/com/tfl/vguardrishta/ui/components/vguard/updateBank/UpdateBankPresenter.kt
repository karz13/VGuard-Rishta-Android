package com.tfl.vguardrishta.ui.components.vguard.updateBank

import android.content.Context
import android.os.AsyncTask
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.BankTransferUseCase
import com.tfl.vguardrishta.domain.GetKycIdTypesUseCase
import com.tfl.vguardrishta.domain.GetRishtaUserProfileUseCase
import com.tfl.vguardrishta.domain.KycUpdateUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.ui.components.vguard.updateKyc.UpdateKycContract
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.FileUtils
import javax.inject.Inject

class UpdateBankPresenter @Inject constructor(
    val kycUseCase: KycUpdateUseCase,
    val context: Context,
    val getKycIdTypesUseCase: GetKycIdTypesUseCase,
    val bankTransferUseCase: BankTransferUseCase,
    val getRishtaUserProfileUseCase: GetRishtaUserProfileUseCase

    ) : BasePresenter<UpdateBankContract.View>(), UpdateBankContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun validate(bankDetail: BankDetail) {
        if (bankDetail.bankAccNo.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_bank_account_number))
            return
        }
        if (bankDetail.bankAccHolderName.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_account_holder_name))
            return
        }
        if(CacheUtils.getRishtaUserType() == Constants.INF_USER_TYPE)
        {
        if (bankDetail.bankAccType.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_bank_aacounttype))
            return
        }
        }
        if (bankDetail.bankNameAndBranch.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_bank_name))
            return
        }

        if (bankDetail.bankIfsc.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_enter_valid_ifcsc))
            return
        }
        if (bankDetail.bankIfsc!!.length!=11) {
            getView()?.showToast(context.getString(R.string.please_enter_valid_ifcsc2))
            return
        }
        val checkBook = CacheUtils.getFileUploader().getChequeBookPhotoFile()
        if (checkBook==null && bankDetail.checkPhoto.isNullOrEmpty()) {
            getView()?.showToast(context.getString(R.string.please_upload_cheque))
            return
        }
        uploadPhotos(bankDetail)
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

    private fun processBanks(it: List<BankDetail>?) {
        val arrayList = CacheUtils.getFirstBank(context)
        if (!it.isNullOrEmpty()) {
            arrayList.addAll(it)
        }
        CacheUtils.setBanks(arrayList)
        getView()?.showBanks(arrayList)
    }

    override fun updateBankDetails(
        bankDetails: BankDetail
    ) {
        disposables?.add(getRishtaUserProfileUseCase.updateBank(bankDetails).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processResult(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
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
                getView()?.showKycDetails(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )

    }


    private fun uploadPhotos(bankDetails: BankDetail) {
        object : AsyncTask<Void, Void, Status>() {

            override fun onPreExecute() {
                getView()?.showProgress()
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): com.tfl.vguardrishta.models.Status? {

                val checkBook = CacheUtils.getFileUploader().getChequeBookPhotoFile()
                if (checkBook != null) {
                    val entityUid = FileUtils.sendImage(checkBook, Constants.CHEQUE, "")
                    if (!entityUid.isNullOrEmpty()) {
                        bankDetails?.checkPhoto = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }

                return Status(200, "SUCCESS")
            }

            override fun onPostExecute(result: com.tfl.vguardrishta.models.Status?) {
                getView()?.stopProgress()
                if (result!!.code == 200) {
                    updateBankDetails(bankDetails)
                } else {
                    getView()?.showMsgDialog("File upload issue")
                }
            }
        }.execute()

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
}
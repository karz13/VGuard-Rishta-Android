package com.tfl.vguardrishta.ui.components.vguard.bankTransfer

import android.content.Context
import android.os.AsyncTask
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.AuthenticateUserCaseCase
import com.tfl.vguardrishta.domain.BankTransferUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.BankDetail
import com.tfl.vguardrishta.models.ProductOrder
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.FileUtils
import javax.inject.Inject

class BankTransferPresenter @Inject constructor(
    val context: Context,
    val bankTransferUseCase: BankTransferUseCase,
    val authenticateUserCaseCase: AuthenticateUserCaseCase

) : BasePresenter<BankTransferContract.View>(), BankTransferContract.Presenter {

    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun validateDetail(
        amount: String,
        accNo: String,
        accHolderName: String,
        accType: String,
        bankAndBranch: String,
        ifscCode: String,
        selectedBankDetail: String
    ) {

        if (amount.isEmpty()) {
            getView()?.showToast(context.getString(R.string.enter_amount))
            return
        }


//        if (accNo.isEmpty()) {
//            getView()?.showToast(context.getString(R.string.enter_account_number))
//            return
//        }
//
//        if (accHolderName.isEmpty()) {
//            getView()?.showToast(context.getString(R.string.enter_account_holder_name))
//            return
//        }
//
//
//        if (accType.isEmpty()) {
//            getView()?.showToast(context.getString(R.string.select_account_type))
//            return
//        }
//
//        if (selectedBankDetail.bankId == -1) {
//            getView()?.showToast(context.getString(R.string.select_bank))
//            return
//        }

//        if (bankAndBranch.isEmpty()) {
//            getView()?.showToast(context.getString(R.string.enter_bank_name_branc_name))
//            return
//        }

//        if (ifscCode.isEmpty()) {
//            getView()?.showToast(context.getString(R.string.enter_ifsc))
//            return
//        }

        val productOrder = ProductOrder()
        val bankDetails = BankDetail()
        productOrder.amount = amount
        bankDetails.bankAccNo = accNo
        bankDetails.bankAccHolderName = accHolderName
        bankDetails.bankAccType = accType
        bankDetails.bankNameAndBranch = bankAndBranch
        bankDetails.bankIfsc = ifscCode
        productOrder.bankDetail = bankDetails
        uploadPhotos(productOrder)

//        if(CacheUtils.getFileUploader().getChequeBookPhotoFile()!=null){
//        }else{
//            sendOrder(productOrder)
//        }

    }


    private fun uploadPhotos(po: ProductOrder) {
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
                        po.bankDetail?.checkPhoto = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }
                return Status(200, "SUCCESS")
            }

            override fun onPostExecute(result: com.tfl.vguardrishta.models.Status?) {
                getView()?.stopProgress()
                if (result!!.code == 200) {
                    sendOrder(po)
                } else {
                    getView()?.showToast("File upload issue")
                }
            }
        }.execute()
    }

    private fun sendOrder(po: ProductOrder) {
        disposables?.add(bankTransferUseCase.execute(po).applySchedulers()
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
            getView()?.showMsgDialogWithFinish(it.message!!)
            CacheUtils.refreshView(true)

        } else if (it.code == 400) {
            getView()?.showMsgDialog(it.message!!)
        }
    }

    override fun getUserBankDetail() {
        disposables?.add(bankTransferUseCase.getBankDetail().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    getView()?.showBankDetail(it)
                }
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
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

    override fun getProfile() {
        disposables?.add(authenticateUserCaseCase.getUser().applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                if (it != null) {
                    getView()?.setRishtaUser(it)
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


}
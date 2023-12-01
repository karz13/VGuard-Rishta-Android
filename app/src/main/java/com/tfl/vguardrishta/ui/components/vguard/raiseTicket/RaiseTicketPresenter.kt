package com.tfl.vguardrishta.ui.components.vguard.raiseTicket

import android.content.Context
import android.os.AsyncTask
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.domain.GetTicketTypeUseCase
import com.tfl.vguardrishta.domain.SendTicketUseCase
import com.tfl.vguardrishta.extensions.applySchedulers
import com.tfl.vguardrishta.models.RaiseTicket
import com.tfl.vguardrishta.models.Status
import com.tfl.vguardrishta.models.TicketType
import com.tfl.vguardrishta.ui.base.BasePresenter
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.FileUtils
import javax.inject.Inject

class RaiseTicketPresenter @Inject constructor(
    val getTicketTypeUseCase: GetTicketTypeUseCase,
    val context: Context,
    val sendTicketUseCase: SendTicketUseCase
) : BasePresenter<RaiseTicketContract.View>(), RaiseTicketContract.Presenter {


    override fun onCreated() {
        super.onCreated()
        getView()?.initUI()
    }

    override fun getTicketTypes() {
        disposables?.add(
            getTicketTypeUseCase.execute(Unit).applySchedulers()
                .doOnSubscribe { getView()?.showProgress() }.doFinally { getView()?.stopProgress() }
                .subscribe({
                    if (it != null) {
                        val firstTicketType = CacheUtils.getFirstTicketType(context)
                        firstTicketType.addAll(it)
                        getView()?.showTicketTypes(firstTicketType)
                    }
                },
                    {
                        getView()?.showToast(context.getString(R.string.something_wrong))
                    })
        )
    }


    private fun uploadPhotos(rt: RaiseTicket) {

        object : AsyncTask<Void, Void, Status>() {

            override fun onPreExecute() {
                getView()?.showProgress()
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): com.tfl.vguardrishta.models.Status? {

                val issueFile = CacheUtils.getFileUploader().getIssuePhotoFile()
                if (issueFile != null) {
                    val entityUid = FileUtils.sendImage(issueFile, Constants.TICKET, "")
                    if (!entityUid.isNullOrEmpty()) {
                        rt.imagePath = entityUid
                    } else {
                        return Status(400, "Failed")
                    }
                }
                return Status(200, "SUCCESS")
            }

            override fun onPostExecute(result: com.tfl.vguardrishta.models.Status?) {
                getView()?.stopProgress()
                if (result!!.code == 200) {
                    sendTicket(rt)
                } else {
                    getView()?.showToast("File upload issue")
                }
            }
        }.execute()

    }

    private fun sendTicket(rt: RaiseTicket) {

        disposables?.add(sendTicketUseCase.execute(rt).applySchedulers()
            .doOnSubscribe { getView()?.showProgress() }
            .doFinally { getView()?.stopProgress() }
            .subscribe({
                processResult(it)
            }, {
                getView()?.showToast(context.getString(R.string.something_wrong))
            })
        )
    }

    override fun submit(ticketType: TicketType, issueDesc: String) {

        if (ticketType.issueTypeId == -1) {
            getView()?.showToast(context.getString(R.string.select_issue))
            return
        }

//        if(issueDesc.isNullOrEmpty()){
//            getView()?.showToast(context.getString(R.string.enter_issue_desc))
//            return
//        }
//        if(issueDesc.length<30){
//            getView()?.showToast(context.getString(R.string.explain_your_problem_maximum_300_characters))
//            return
//        }

        val raiseTicket = RaiseTicket()
        raiseTicket.description = issueDesc
        raiseTicket.issueTypeId = ticketType.issueTypeId
        uploadPhotos(raiseTicket)
    }

    private fun processResult(it: Status?) {
        if (it == null) {
            getView()?.showToast(context.getString(R.string.problem_occurred))
            return
        }
        if (it.code == 200) {
            getView()?.showMsgDialogWithFinish(it.message!!)
        } else if (it.code == 400) {
            getView()?.showMsgDialog(it.message!!)
        }
    }
}
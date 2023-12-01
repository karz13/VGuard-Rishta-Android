package com.tfl.vguardrishta.ui.components.vguard.fragment.contactUs

import android.view.View
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.vguard.home.RishtaHomeActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.fragment_contact_us.*
import javax.inject.Inject

class ContactUsFragment :
    BaseFragment<ContactUsContract.View, ContactUsContract.Presenter>(),
    ContactUsContract.View, View.OnClickListener {

    @Inject
    lateinit var contactUsPresenter: ContactUsPresenter


    private lateinit var progress: Progress

    override fun initPresenter(): ContactUsContract.Presenter {
        return contactUsPresenter
    }

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_contact_us
    }

    override fun initUI() {
        progress = Progress(context, R.string.please_wait)
        (activity as RishtaHomeActivity).updateCustomToolbar(
            Constants.CONTACT_US,
            getString(R.string.contact_us_),
            ""
        )

        llContactNo.setOnClickListener {
            AppUtils.showPhoneDialer(context!!, tvContactNo.text.toString())
        }

        llSupportMail.setOnClickListener {
            AppUtils.showComposeMail(context!!, tvSupportMail.text.toString())
        }

        llWhatsAppNo.setOnClickListener {
            AppUtils.launchWhatsApp(context!!, tvWhatsappNo.text.toString())
        }

        tvVgCorporateOffice.text =
            "V-Guard Industries Ltd. \n " +
                    "Regd. Office: \n" +
                    " 42/962, Vennala Highschool Road \n " +
                    "Vennala, Kochi-682028 \n" +
                    " Ph: +91 484 433 5000 \n" +
                    " Fax: +91 484 300 5100 \n" +
                    " Email: mail@vguard.in"
    }


    override fun showProgress() {
        progress.show()
    }

    override fun stopProgress() {
        progress.dismiss()
    }

    override fun showToast(toast: String) {
        activity?.toast(toast)
    }

    override fun showError() {
        activity?.toast(resources.getString(R.string.something_wrong))
    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

}
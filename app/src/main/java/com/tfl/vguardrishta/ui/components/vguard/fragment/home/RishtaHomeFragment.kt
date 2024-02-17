package com.tfl.vguardrishta.ui.components.vguard.fragment.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.tfl.vguardrishta.BuildConfig
import com.bumptech.glide.Glide
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.extensions.visible
import com.tfl.vguardrishta.extensions.withDelay
import com.tfl.vguardrishta.models.WelcomeBanner
import com.tfl.vguardrishta.remote.ApiService
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.vguard.airCooler.AirCoolerActivity
import com.tfl.vguardrishta.ui.components.vguard.dashboard.DashboardActivity
import com.tfl.vguardrishta.ui.components.vguard.engagement.EngagementActivity
import com.tfl.vguardrishta.ui.components.vguard.home.RishtaHomeActivity
import com.tfl.vguardrishta.ui.components.vguard.infodesk.InfoDeskActivity
import com.tfl.vguardrishta.ui.components.vguard.raiseTicket.RaiseTicketActivity
import com.tfl.vguardrishta.ui.components.vguard.redeempoints.RedeemPointActivity
import com.tfl.vguardrishta.ui.components.vguard.redemptionHistory.RedemptionHistoryActivity
import com.tfl.vguardrishta.ui.components.vguard.registerProduct.RegisterProductListActivity
import com.tfl.vguardrishta.ui.components.vguard.scanCode.ScanCodeActivity
import com.tfl.vguardrishta.ui.components.vguard.scanHistory.ScanCodeHistoryActivity
import com.tfl.vguardrishta.ui.components.vguard.schemes.SchemesAndOffersActivity
import com.tfl.vguardrishta.ui.components.vguard.tds.TDSCertificateActivity
import com.tfl.vguardrishta.ui.components.vguard.tdsStatement.TDSstatementActivity
import com.tfl.vguardrishta.ui.components.vguard.tierDetail.TierDetailActivity
import com.tfl.vguardrishta.ui.components.vguard.updateBank.UpdateBankActivity
import com.tfl.vguardrishta.ui.components.vguard.updateKyc.UpdateKycActivity
import com.tfl.vguardrishta.ui.components.vguard.updateKycRetailer.UpdateKycReatilerActivity
import com.tfl.vguardrishta.ui.components.vguard.welfare.WelfareActivity
import com.tfl.vguardrishta.ui.components.vguard.whats_new.WhatsNewActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.fragment_rishta_home.*
import kotlinx.android.synthetic.main.help_details_layout.*
import javax.inject.Inject

class RishtaHomeFragment : BaseFragment<RishtaHomeContract.View, RishtaHomeContract.Presenter>(),
    RishtaHomeContract.View, View.OnClickListener {

    @Inject
    lateinit var rishtaHomePresenter: RishtaHomePresenter

    private lateinit var progress: Progress

    override fun initPresenter(): RishtaHomeContract.Presenter {
        return rishtaHomePresenter
    }

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_rishta_home
    }

    override fun initUI() {
        progress = Progress(context, R.string.please_wait)
        (activity as RishtaHomeActivity).updateCustomToolbar(Constants.HOME_SCREEN, "", "")

        val user=CacheUtils.getRishtaUser()
        if (user.roleId == "2") {
            tvScanCode.setText(getString(R.string.product_registration))
            llTdsStatement.visibility=View.VISIBLE
            llTdsStatement.setOnClickListener(this)
        }
        if(user.roleId=="1"&& (user.professionId==1||user.professionId==2||user.professionId==3))
        {
            llTdsStatement.visibility=View.VISIBLE
            ivTds.setImageDrawable(resources.getDrawable(R.drawable.ic_instruction_manual))
            tvTdsText.text="Instruction\nManual"
            llTdsStatement.setOnClickListener(this)
        }
        llScanCode.setOnClickListener(this)
        llRedeemPoints.setOnClickListener(this)
        llDashboard.setOnClickListener(this)
        llSchemeOffers.setOnClickListener(this)
        llInfoDesk.setOnClickListener(this)
        ll_whats_new.setOnClickListener(this)
        llUpdateKyc.setOnClickListener(this)
        llRaiseTicket.setOnClickListener(this)
        llEngagement.setOnClickListener(this)
        llUserInfoDet.setOnClickListener(this)

        llPointsBalance.setOnClickListener(this)
        llRedeemedPoints.setOnClickListener(this)
        llNumberOfScans.setOnClickListener(this)
        llupdateBack.setOnClickListener(this)
        llTdsCertificate.setOnClickListener(this)
        /*if (CacheUtils.getRishtaUserType() == Constants.RET_USER_TYPE && CacheUtils.getRishtaUser().airCoolerEnabled == Constants.AIR_COOLER_ENABLED) {
            llAirCooler.setOnClickListener(this)
            llAirCooler.visible = true
        } else {
            llWelfare.setOnClickListener(this)
            llWelfare.visible = true
        }
*/
        if (CacheUtils.getRishtaUserType() == Constants.RET_USER_TYPE)
        {
            tvUpdateKyc .text = activity?.getString(R.string.update_pan)
        }

        if (CacheUtils.getRishtaUserType() == Constants.RET_USER_TYPE) {
            llAirCooler.setOnClickListener(this)
            llAirCooler.visible = true
        } else {
            llWelfare.setOnClickListener(this)
            llWelfare.visible = true
        }
        showUserDetails()

        tvContactNo.setOnClickListener {
            AppUtils.showPhoneDialer(context!!, tvContactNo.text.toString())
        }

        tvSupportMail.setOnClickListener {
            AppUtils.showComposeMail(context!!, tvSupportMail.text.toString())
        }

        tvWhatsappNo.setOnClickListener {
            AppUtils.launchWhatsApp(context!!, tvWhatsappNo.text.toString())
        }

        ivClub.setOnClickListener {
            activity?.launchActivity<TierDetailActivity> { }
        }

        val rishtaUser = CacheUtils.getRishtaUser()
        if (rishtaUser.roleId == Constants.RET_USER_TYPE) {
//            ivSchemWise.setColorFilter(ContextCompat.getColor(this, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
            /*   ImageViewCompat.setImageTintList(
                   ivWhatsNew,
                   ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
               );*/

            //   tvWhatsNew.setTextColor(resources.getColor(R.color.grey))

            ImageViewCompat.setImageTintList(
                ivWelfare,
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
            );

            tvWelfare.setTextColor(resources.getColor(R.color.grey))
            ImageViewCompat.setImageTintList(
                ivUpdateKyc,
                ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grey))
            );

            // tvUpdateKyc.setTextColor(resources.getColor(R.color.grey))
            // tvAirCooler.setTextColor(resources.getColor(R.color.grey))
        }
    }

    private fun showRedirectPlayStore() {
        val dbAppVersion = CacheUtils.getDbAppVersion()
        if (dbAppVersion != null && dbAppVersion != BuildConfig.VERSION_CODE) {
            AppUtils.redirectToPlayStore(context!!, getString(R.string.update))
        }
    }

    override fun onResume() {
        super.onResume()
        rishtaHomePresenter.getUserDetails()

        val rishtaUser = CacheUtils.getRishtaUser()

        if (CacheUtils.isShowWelcomeBanner() && !rishtaUser.welcomePointsMsg.isNullOrEmpty() /*&& rishtaUser.welcomePointsErrorCode == 1*/)  {
            withDelay(200) {
                showWelcomeDialog(layoutInflater, context!!, rishtaUser.welcomeBanner!!)
            }
        }
        showRedirectPlayStore()
    }

    override fun autoLogoutUser() {
        (activity as RishtaHomeActivity).autoLogoutUser()
    }

    override fun showNotificationCount(count: Int) {
        (activity as RishtaHomeActivity).showNotificationCount(count)
    }

    override fun showUserDetails() {
        val vu = CacheUtils.getRishtaUser()
        tvDisplayName.text = vu.name?.toUpperCase()
        tvUserCode.text = vu.userCode
        var selfie = vu.kycDetails.selfie
        selfie = AppUtils.getSelfieUrl() + selfie
        Glide.with(this).load(selfie)
            .placeholder(R.drawable.ic_v_guards_user).into(ivUserImage)
        val pointsSummary = vu.pointsSummary
        tvRedeemedPoints.text = pointsSummary?.redeemedPoints ?: "0.00"
        tvNumberOfScans.text = pointsSummary?.numberOfScan ?: "0"
        tvPointsBalance.text = pointsSummary?.pointsBalance ?: "0.00"

        if (vu.welcomePointsErrorCode == 1) {
            withDelay(200) {
                try {
                    showWelcomeDialog(layoutInflater, context!!, vu.welcomeBanner!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (vu.tierFlag == "1") {
            ivClub.visible = true
        }

        showRedirectPlayStore()
    }

    private fun showWelcomeDialog(layoutInflater: LayoutInflater, context: Context, welcomeBanner: WelcomeBanner) {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dilog_welcome_msg, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val ivClose = dialogView.findViewById(R.id.ivClose) as ImageView
        val tvErrorMsg = dialogView.findViewById(R.id.tvErrorMsg) as TextView
        val ivImage = dialogView.findViewById(R.id.ivFirstImage) as ImageView
        val tvVid = dialogView.findViewById(R.id.email_ok) as TextView
        if (!welcomeBanner.imgPath.isNullOrEmpty()) {
            ivImage.visibility = View.VISIBLE
            tvErrorMsg.visibility = View.GONE
            Glide.with(context).load(ApiService.imageBaseUrl + welcomeBanner.imgPath).into(ivImage)
        } else {
            ivImage.visibility = View.GONE
            tvErrorMsg.visibility = View.VISIBLE
            tvErrorMsg.text = welcomeBanner.textMessage
        }
        if(!welcomeBanner.vdoText.isNullOrEmpty()) {
            //tvVid.text = welcomeBanner.vdoText
        }

        if (!welcomeBanner.videoPath.isNullOrEmpty()) {
            if(welcomeBanner.videoPath!="Update PAN" && welcomeBanner.videoPath!="Update Kyc") {
                tvVid.visibility = View.VISIBLE
                tvVid.setOnClickListener {
                    AppUtils.openPDFWithUrl(context, welcomeBanner.videoPath)
                    dialog.dismiss()
                }
            }
            if(welcomeBanner.videoPath=="Update PAN" || welcomeBanner.videoPath=="Update Kyc")
            {
                showUpdateKycDialog(this.layoutInflater, context, welcomeBanner)
            }
              //AppUtils.openPDFWithUrl(context, welcomeBanner.videoPath)
        }

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        if(welcomeBanner.code ==1 )
            dialog.show()

        dialog.setOnCancelListener {

        }
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
    }



    private fun showUpdateKycDialog(layoutInflater: LayoutInflater, context: Context, welcomeBanner: WelcomeBanner) {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater

        val dialogView = inflater.inflate(R.layout.dilog_welcome_msg, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val ivClose = dialogView.findViewById(R.id.ivClose) as ImageView
        val tvErrorMsg = dialogView.findViewById(R.id.tvErrorMsg) as TextView
        val ivImage = dialogView.findViewById(R.id.ivFirstImage) as ImageView
        val tvVid = dialogView.findViewById(R.id.email_ok) as TextView
        if (!welcomeBanner.imgPath.isNullOrEmpty()) {
            ivImage.visibility = View.VISIBLE
            tvErrorMsg.visibility = View.GONE
            Glide.with(context).load(ApiService.imageBaseUrl + welcomeBanner.imgPath).into(ivImage)
        } else {
            ivImage.visibility = View.GONE
            tvErrorMsg.visibility = View.VISIBLE
            tvErrorMsg.text = welcomeBanner.textMessage
        }


        if (!welcomeBanner.videoPath.isNullOrEmpty() && welcomeBanner.code ==1   ) {
            if(welcomeBanner.videoPath=="Update PAN" || welcomeBanner.videoPath=="Update Kyc")
            {
                tvVid.visibility = View.VISIBLE
                tvVid.text = welcomeBanner.videoPath
                if(welcomeBanner.videoPath=="Update PAN")
                {
                    tvVid.setOnClickListener {
                        activity?.launchActivity<UpdateKycReatilerActivity> { }
                        dialog.dismiss()
                    }
                }
                else
                    if(welcomeBanner.videoPath=="Update Kyc")
                    {
                        tvVid.setOnClickListener {
                            activity?.launchActivity<UpdateKycActivity> { }
                            dialog.dismiss()
                        }
                    }
            }

            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            dialog.show()

            dialog.setOnCancelListener {

            }
            ivClose.setOnClickListener {
                dialog.dismiss()
            }
        }


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
            R.id.llScanCode -> {
              if (CacheUtils.getRishtaUser().roleId == Constants.RET_USER_TYPE) {
                        activity?.launchActivity<RegisterProductListActivity> { }
                    } else {
                        activity?.launchActivity<ScanCodeActivity> { }
                    }
            }
            R.id.llRedeemPoints -> {
                activity?.launchActivity<RedeemPointActivity> { }
            }
            R.id.llDashboard -> {
                activity?.launchActivity<DashboardActivity> { }
            }

            R.id.llSchemeOffers -> {
                activity?.launchActivity<SchemesAndOffersActivity> { }
            }

            R.id.llInfoDesk -> {
                activity?.launchActivity<InfoDeskActivity> { }
            }

            R.id.ll_whats_new -> {
                activity?.launchActivity<WhatsNewActivity> { }
                /*  if (CacheUtils.getRishtaUser().roleId == Constants.INF_USER_TYPE) {
                      activity?.launchActivity<WhatsNewActivity> { }
                  } else {
                      AppUtils.showErrorDialog(layoutInflater,
                          context!!,
                          getString(R.string.coming_soon))
                  }*/
            }

            R.id.llUpdateKyc -> {
                if (CacheUtils.getRishtaUser().roleId == Constants.INF_USER_TYPE) {
                    activity?.launchActivity<UpdateKycActivity> { }
                } else {
                    /*    AppUtils.showErrorDialog(
                            layoutInflater,
                            context!!,
                            getString(R.string.coming_soon)
                        )*/
                    activity?.launchActivity<UpdateKycReatilerActivity> { }
                }

            }

            R.id.ivBack -> {
                activity?.onBackPressed()
            }

            R.id.llRaiseTicket -> {
                activity?.launchActivity<RaiseTicketActivity> { }
            }

            R.id.llWelfare -> {
                if (CacheUtils.getRishtaUser().roleId == Constants.INF_USER_TYPE) {
                    activity?.launchActivity<WelfareActivity> { }
                } else {
                    AppUtils.showErrorDialog(
                        layoutInflater,
                        context!!,
                        getString(R.string.coming_soon)
                    )
                }
            }

            R.id.llUserInfoDet -> {
                (activity as RishtaHomeActivity).showProfileScreen()
            }

            R.id.llPointsBalance -> {
                activity?.launchActivity<DashboardActivity> { }
            }

            R.id.llRedeemedPoints -> {
                activity?.launchActivity<RedemptionHistoryActivity> { }

            }

            R.id.llNumberOfScans -> {
                activity?.launchActivity<ScanCodeHistoryActivity> { }

            }
            R.id.llupdateBack -> {
                activity?.launchActivity<UpdateBankActivity> { }

            }

            R.id.llTdsCertificate -> {
                activity?.launchActivity<TDSCertificateActivity> { }

            }

            R.id.llAirCooler -> {
                activity?.launchActivity<AirCoolerActivity> { }

            }
            R.id.llEngagement->{
                activity?.launchActivity<EngagementActivity> {  }
            }
            R.id.llTdsStatement -> {
                if(CacheUtils.getRishtaUser().roleId=="2") {
                    activity?.launchActivity<TDSstatementActivity> { }
                }
                else
                {
                    AppUtils.openPDFWithUrl((activity as RishtaHomeActivity), "https://www.vguardrishta.com/img/appImages/Instructionmanual.pdf")

                }

            }
        }
    }

}
package com.tfl.vguardrishta.di.components

import com.tfl.vguardrishta.di.modules.ApplicationModule
import com.tfl.vguardrishta.di.modules.NetworkModule
import com.tfl.vguardrishta.di.scope.ApplicationScope
import com.tfl.vguardrishta.ui.components.dashboard.DashboardActivity
import com.tfl.vguardrishta.ui.components.logIn.LogInActivity
import com.tfl.vguardrishta.ui.components.reupdateKyc.*
import com.tfl.vguardrishta.ui.components.splash.SplashActivity
import com.tfl.vguardrishta.ui.components.vguard.activeSchemeOffers.ActiveSchemeOffersActivity
import com.tfl.vguardrishta.ui.components.vguard.airCooler.AirCoolerActivity
import com.tfl.vguardrishta.ui.components.vguard.aircoolerProductRegistration.AirCoolerProductRegistrationActivity
import com.tfl.vguardrishta.ui.components.vguard.bankTransfer.BankTransferActivity
import com.tfl.vguardrishta.ui.components.vguard.bonusRewards.BonusRewardsActivity
import com.tfl.vguardrishta.ui.components.vguard.dailyWinner.DailyWinnerActivity
import com.tfl.vguardrishta.ui.components.vguard.downloads.DownloadsActivity
import com.tfl.vguardrishta.ui.components.vguard.engagement.EngagementActivity
import com.tfl.vguardrishta.ui.components.vguard.forgotPassword.ForgotPasswordActivity
import com.tfl.vguardrishta.ui.components.vguard.forgotPassword.ForgotPasswordFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.alert.NotificationFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.contactUs.ContactUsFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.home.RishtaHomeFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.login.FirstStartFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.login.LoginFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.newuser.*
import com.tfl.vguardrishta.ui.components.vguard.fragment.otp.OtpVerifyFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.otpForReverify.SendOtpForReVerifyFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.profile.RishtaUserProfileFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.retailerProfile.RetailerUserProfileFragment
import com.tfl.vguardrishta.ui.components.vguard.home.RishtaHomeActivity
import com.tfl.vguardrishta.ui.components.vguard.infodesk.InfoDeskActivity
import com.tfl.vguardrishta.ui.components.vguard.loginWithOtp.LoginWithOtpActivity
import com.tfl.vguardrishta.ui.components.vguard.loginWithOtp.LoginWithOtpFragment
import com.tfl.vguardrishta.ui.components.vguard.paytm.PaytmTransferActivity
import com.tfl.vguardrishta.ui.components.vguard.productCatalog.ProductCatalogActivity
import com.tfl.vguardrishta.ui.components.vguard.productRegisteration.ProductRegistrationActivity
import com.tfl.vguardrishta.ui.components.vguard.productWiseEarnings.ProductWiseEarningActivity
import com.tfl.vguardrishta.ui.components.vguard.productWiseOfferDetail.ProductWiseOffersDetailActivity
import com.tfl.vguardrishta.ui.components.vguard.productWiseOffers.ProductWiseOffersActivity
import com.tfl.vguardrishta.ui.components.vguard.raiseTicket.RaiseTicketActivity
import com.tfl.vguardrishta.ui.components.vguard.redeempoints.RedeemPointActivity
import com.tfl.vguardrishta.ui.components.vguard.redeemproducts.RedeemProductsActivity
import com.tfl.vguardrishta.ui.components.vguard.referralCode.ReferralCodeActivity
import com.tfl.vguardrishta.ui.components.vguard.registerProduct.AddCustomerDetFragment
import com.tfl.vguardrishta.ui.components.vguard.registerProduct.RegisterProductActivity
import com.tfl.vguardrishta.ui.components.vguard.registerProduct.RegisterProductFragment
import com.tfl.vguardrishta.ui.components.vguard.registerProduct.RegisterProductListActivity
import com.tfl.vguardrishta.ui.components.vguard.returnScan.ReturnScanActivity
import com.tfl.vguardrishta.ui.components.vguard.scanCode.ScanCodeActivity
import com.tfl.vguardrishta.ui.components.vguard.scanHistory.ScanCodeHistoryActivity
import com.tfl.vguardrishta.ui.components.vguard.schemeWiseEarning.SchemeWiseEarningActivity
import com.tfl.vguardrishta.ui.components.vguard.schemes.SchemesAndOffersActivity
import com.tfl.vguardrishta.ui.components.vguard.specialComboOffers.SchemeProgressActivity
import com.tfl.vguardrishta.ui.components.vguard.specialComboOffers.SpecialComboOffersActivity
import com.tfl.vguardrishta.ui.components.vguard.tds.TDSCertificateActivity
import com.tfl.vguardrishta.ui.components.vguard.tdsStatement.TDSstatementActivity
import com.tfl.vguardrishta.ui.components.vguard.ticketHistory.TicketHistoryActivity
import com.tfl.vguardrishta.ui.components.vguard.tierDetail.TierDetailActivity
import com.tfl.vguardrishta.ui.components.vguard.updateKyc.UpdateKycActivity
import com.tfl.vguardrishta.ui.components.vguard.updateBank.UpdateBankActivity
import com.tfl.vguardrishta.ui.components.vguard.updateKycRetailer.UpdateKycReatilerActivity
import com.tfl.vguardrishta.ui.components.vguard.updateRetailerProfile.UpdateRetailerProfileActivity
import com.tfl.vguardrishta.ui.components.vguard.uploadScanError.UploadScanErrorActivity
import com.tfl.vguardrishta.ui.components.vguard.viewcart.ViewCartActivity
import com.tfl.vguardrishta.ui.components.vguard.welfare.WelfareActivity
import com.tfl.vguardrishta.ui.components.vguard.whats_new.WhatsNewActivity
import dagger.Component
import com.tfl.vguardrishta.ui.components.vguard.redemptionHistory.RedemptionHistoryActivity as RedemptionHistoryActivity1
import com.tfl.vguardrishta.ui.components.vguard.updateProfile.UpdateProfileActivity as UpdateProfileActivity1

/**
 * Created by Shanmuka on 19-11-2018.
 */
@ApplicationScope
@Component(modules = [ApplicationModule::class, NetworkModule::class])
interface ApplicationComponent {
    fun inject(logInActivity: LogInActivity)
    fun inject(signInActivity: SplashActivity)
    fun inject(scanCodeActivity: ScanCodeActivity)
    fun inject(dashboardActivity: DashboardActivity)
    fun inject(downloadsActivity: DownloadsActivity)
    fun inject(loginWithOtpFragment: LoginWithOtpFragment)
    fun inject(loginWithOtpActivity: LoginWithOtpActivity)
    fun inject(forgotPasswordFragment: ForgotPasswordFragment)
    fun inject(forgotPasswordActivity: ForgotPasswordActivity)

    fun inject(rishtaHomeActivity: RishtaHomeActivity)
    fun inject(redeemPointActivity: RedeemPointActivity)
    fun inject(redeemProductsActivity: RedeemProductsActivity)
    fun inject(viewCartActivity: ViewCartActivity)

    fun inject(dashboardActivity: com.tfl.vguardrishta.ui.components.vguard.dashboard.DashboardActivity)
    fun inject(schemesAndOffersActivity: SchemesAndOffersActivity)
    fun inject(infoDeskActivity: InfoDeskActivity)
    fun inject(whatsNewActivity: WhatsNewActivity)
    fun inject(firstStartFragment: FirstStartFragment)
    fun inject(loginFragment: LoginFragment)
    fun inject(otpVerifyFragment: OtpVerifyFragment)
    fun inject(mobileRegistrationFragment: MobileRegistrationFragment)
    fun inject(newUserRegistrationActivity: NewUserRegistrationActivity)
    fun inject(personalDetailsFragment: PersonalDetailsFragment)
    fun inject(bankNomineeDetailsFragment: BankNomineeDetailsFragment)
    fun inject(previewNewUserFragment: PreviewNewUserFragment)
    fun inject(updateKycActivity: UpdateKycActivity)
    fun inject(updateKycReatilerActivity: UpdateKycReatilerActivity)
    fun inject(paytmTransferActivity: PaytmTransferActivity)
    fun inject(bankTransferActivity: BankTransferActivity)
    fun inject(raiseTicketActivity: RaiseTicketActivity)
    fun inject(ticketHistoryActivity: TicketHistoryActivity)
    fun inject(rishtaHomeFragment: RishtaHomeFragment)
    fun inject(notificationFragment: NotificationFragment)
    fun inject(newUserKycFragment: NewUserKycFragment)
    fun inject(rishtaUserProfileFragment: RishtaUserProfileFragment)
    fun inject(bonusRewardsActivity: BonusRewardsActivity)
    fun inject(productWiseOffersActivity: ProductWiseOffersActivity)
    fun inject(redemptionHistoryActivity: RedemptionHistoryActivity1)
    fun inject(contactUsFragment: ContactUsFragment)
    fun inject(scanCodeHistoryActivity: ScanCodeHistoryActivity)
    fun inject(updateProfileActivity: UpdateProfileActivity1)
    fun inject(productWiseOffersDetailActivity: ProductWiseOffersDetailActivity)
    fun inject(productWiseEarningActivity: ProductWiseEarningActivity)
    fun inject(schemeWiseEarningActivity: SchemeWiseEarningActivity)
    fun inject(loginWithOtpFragment: com.tfl.vguardrishta.ui.components.vguard.fragment.loginWithOtp.LoginWithOtpFragment)
    fun inject(activeSchemeOffersActivity: ActiveSchemeOffersActivity)
    fun inject(reUpdateKycFragment: ReUpdateKycFragment)
    fun inject(reUpdateKycActivity: ReUpdateKycActivity)
    fun inject(sendOtpForReVerifyFragment: SendOtpForReVerifyFragment)
    fun inject(previewReUpdateKycUserFragment: PreviewReUpdateKycUserFragment)
    fun inject(uploadScanErrorActivity: UploadScanErrorActivity)
    fun inject(productCatalogActivity: ProductCatalogActivity)
    fun inject(registerProductActivity: RegisterProductActivity)
    fun inject(registerProductListActivity: RegisterProductListActivity)
    fun inject(registerProductFragment: RegisterProductFragment)
    fun inject(addCustomerDetFragment: AddCustomerDetFragment)
    fun inject(returnScanActivity: ReturnScanActivity)
    fun inject(retailerUserProfileFragment: RetailerUserProfileFragment)
    fun inject(referralCodeActivity: ReferralCodeActivity)
    fun inject(tierDetailActivity: TierDetailActivity)
    fun inject(updateRetailerProfileActivity: UpdateRetailerProfileActivity)
    fun inject(welfareActivity: WelfareActivity)
    fun inject(dailyWinnerActivity: DailyWinnerActivity)
    fun inject(airCoolerActivity: AirCoolerActivity)
    fun inject(airCoolerProductRegistrationActivity: AirCoolerProductRegistrationActivity)
    fun inject(productRegistrationActivity: ProductRegistrationActivity)
    fun inject(retailerReUpdateKycFragment: RetailerReUpdateKycFragment)
    fun inject(previewRetailerUpdateKycUserFragment: PreviewRetailerUpdateKycUserFragment)
    fun inject(updateBankActivity: UpdateBankActivity)
    fun inject(tDSCertificateActivity: TDSCertificateActivity)
    fun inject(tdSstatementActivity: TDSstatementActivity)
    fun inject(engagementActivity: EngagementActivity)
    fun inject(specialComboOffersActivity: SpecialComboOffersActivity)

    fun inject (schemeProgressActivity: SchemeProgressActivity)

}
package com.tfl.vguardrishta.remote

import com.tfl.vguardrishta.models.*
import io.reactivex.Single
import retrofit2.http.*

interface ApiService {

    companion object {

        //val prodUrl: String get() = "http://202.66.175.34:18091/vguard/api/" // live new prod server
         val prodUrl: String get() = "http://34.100.133.239:18092/vguard/api/" // stage new prod server

        //val prodUrl: String get() = "http://103.48.50.249:18092/vguard/api/" // demo server
     // val prodUrl: String get() = "http://202.66.175.34:18093/vguard/api/" //  new stage server
     //  val prodUrl: String get() = "http://192.168.1.2:18091/vguard/api/" //  new stage dev server
      //  val prodUrl: String get() = "http://192.168.1.7:18091/vguard/api/" //  local server

//      val prodUrl: String get() = "http:// 43.252.89.190:18093/vguard/api/" // live test server withh ip

        const val imageBaseUrl: String = "https://vguardrishta.com/"

        const val ecardUrl: String = "${imageBaseUrl}img/appImages/eCard/"
//        val prodUrl: String get() = "http://192.168.0.103:18091/vguard/api/"  // local
//
//                        val prodUrl: String get() = "http://192.168.43.160:18093/vguard/api/"  // local
//        val prodUrl: String get() = "http://192.168.43.79:18093/vguard/api/"  // local

        const val faqUrl: String = "${imageBaseUrl}frequently-questions.html"
        const val tAndCUrl: String = "${imageBaseUrl}tnc.html"

        const val tAndCUrl_Hindi: String = "${imageBaseUrl}tnc%20-hindi_app.html"
        const val faqUrl_Hindi: String = "${imageBaseUrl}frequently-questions-hindi_app.html"

        const val tAndCUrl_Marathi: String = "${imageBaseUrl}tnc%20-marathi_app.html"
        const val faqUrl_Marathi: String = "${imageBaseUrl}frequently-questions-marathi_app.html"

        const val tAndCUrl_Telugu: String = "${imageBaseUrl}tnc%20-telgu_app.html"
        const val faqUrl_Telugu: String = "${imageBaseUrl}frequently-questions-telgu_app.html"

        const val tAndCUrl_Bengali: String = "${imageBaseUrl}tnc%20_bengali_app.html"
        const val faqUrl_Bengali: String = "${imageBaseUrl}frequently-questions-bengali_app.html"

        const val faqUrl_Malayalam: String = "${imageBaseUrl}frequently-questions-malyalam_app.html"
        const val tAndCUrl_Malayalam: String = "${imageBaseUrl}tnc -malyalam_app.html"

        const val faqUrl_Kannada: String = "${imageBaseUrl}frequently-questions-kannada_app.html"
        const val tAndCUrl_Kannada: String = "${imageBaseUrl}tnc -kannada_app.html"

        const val fagUrl_Tamil: String = "${imageBaseUrl}frequently-questions-tamil_app.html"
        const val tAndCUrl_Tamil: String = "${imageBaseUrl}tnc_tamil.html"

        // retailer faq and terms
        const val retFaqUrl: String = "${imageBaseUrl}frequently-questions-retailer.html"
        const val retTAndCUrl: String = "${imageBaseUrl}tnc_retailer.html"
        const val faqUrl_serviceEngineer: String = "${imageBaseUrl}frequently-questions_Ac_Service_Engineer_Program.html"

//        const val imageBaseUrl: String = "http://192.168.0.105:8080/vgImages/"

        val selfieUrl: String get() = "${imageBaseUrl}img/appImages/Profile/"
        const val chequeImagePath: String = "${imageBaseUrl}img/appImages/Cheque/"
        const val idCardUrl: String = "${imageBaseUrl}img/appImages/IdCard/"
        const val panCardUrl: String = "${imageBaseUrl}img/appImages/PanCard/"
        const val gstPicUrl: String = "${imageBaseUrl}img/appImages/GST/"
        const val retSelfieUrl: String = "${imageBaseUrl}retimg/appImages/Profile/"
        const val retIdCardUrl: String = "${imageBaseUrl}retimg/appImages/IdCard/"
        const val retPanCardUrl: String = "${imageBaseUrl}retimg/appImages/PanCard/"
        const val retChequeImagePath: String = "${imageBaseUrl}retimg/appImages/Cheque/"
        const val retGSTPath: String = "${imageBaseUrl}retimg/appImages/GST/"
        const val panCardUrlRet: String = "${imageBaseUrl}retimg/appImages/PanCard/"

        val downLoadURL: String get() = "https://www.vguardrishta.com/"
    }


    @GET("user")
    fun authenticateUser(@Header("Authorization") authRequest: String): Single<User>

    @POST("user/")
    fun getUsers(@Body filter: FilterObject): Single<List<User>>

    @GET("user/dist/")
    fun getDistributorList(): Single<List<User>>

    @POST("transaction/")
    fun getTransactions(@Body filter: FilterObject): Single<List<TransactionHistory>>

    @POST("redemption/history")
    fun getRedemptionList(@Body filter: FilterObject): Single<List<RedemptionOrder>>

    @GET("product/categories")
    fun getProductCategoryList(): Single<List<ProductCategory>>

    @POST("product/catalog")
    fun getProductListing(@Body productRequest: ProductRequest): Single<List<ProductDetail>>

    @GET("state/")
    fun getStates(): Single<List<StateMaster>>

    @GET("district/{stateId}")
    fun getDistricts(@Path("stateId") stateId: Long): Single<List<DistrictMaster>>

    @GET("city/{districtId}")
    fun getCities(@Path("districtId") districtId: Long): Single<List<CityMaster>>
    @POST("product/generateOTP")
    fun productgenerateOtp(@Body otp: OTP): Single<Status>


    @POST("product/registerWarranty")
    fun registerWarranty(@Body cdr: CustomerDetailsRegistration): Single<CouponResponse>

    @GET("schemes/getSpecialSchemeOffers")
    fun getSpecialOffers():Single<List<SpecialSchemes>>

    @POST("user/checkMobileNo")
    fun checkMobileNumber(@Body user: User): Single<Status>

    @POST("user/generateOtp")
    fun generateOtp(@Body user: User): Single<Status>

    @POST("user/signUp")
    fun sendUser(@Body user: User): Single<Status>

    @POST("user/verifyOtp")
    fun verifyOTP(@Body user: User): Single<User>

    @POST("redemption")
    fun redeem(@Body redemption: Redemption): Single<Status>

    @GET("user/relogin")
    fun reLogin(): Single<User>

    @POST("coupon/process")
    fun captureSale(@Body couponDataList: CouponData): Single<CouponResponse>

    @POST("coupon/processForPin")
    fun sendCouponPin(@Body couponDataList: CouponData): Single<CouponResponse>

    @GET("user/{mobileNo}")
    fun getUserByMobile(@Path("mobileNo") mobileNo: String): Single<SPCoupon>

    @POST("order")
    fun uploadOrder(@Body order: Order): Single<Status>

    @GET("order/")
    fun getOrders(): Single<List<Order>>

    @GET("coupon/{coupon}")
    fun searchCoupon(@Path("coupon") coupon: String): Single<SearchCoupon>

    @GET("product/categories")
    fun getCategoryList(): Single<List<Category>>

    @GET("product/getDownloadsData")
    fun getDownloads(): Single<List<DownloadData>>

    @GET("user/version")
    fun getAppVersion(): Single<String>

    @PUT("user/update")
    fun updateUser(@Body user: User): Single<Status>

    @GET("pack/category")
    fun getPackCategoryList(): Single<List<PackCategory>>

    @GET("pack/category/{category}")
    fun getPackProductListing(@Path("category") categoryId: Long): Single<List<PackProduct>>

    @PUT("user/update/token")
    fun updateToken(@Body token: String): Single<Status>

    @GET("couponList/")
    fun getCouponList(): Single<List<RedemptionCouponMaster>>

    @PUT("user/changePassword")
    fun changePassword(@Body changePassword: ChangePassword): Single<Status>

    @POST("user/updateBankDetails")
    fun updateBankDetails(@Body user: User): Single<Status>

    @GET("bank/{ifscCode}")
    fun getByIfsc(@Path("ifscCode") ifscCode: String): Single<BankMaster?>

    @POST("order/update")
    fun updateOrder(@Body token: Long): Single<Status>

    @POST("user/tm/")
    fun getTerritoryManager(@Body filter: FilterObject): Single<List<User>>

    @GET("user/tm/district/{stateId}")
    fun getDistrictMaster(@Path("stateId") stateId: Long): Single<List<DistrictMaster>>

    @POST("invoice")
    fun uploadInvoice(@Body invoice: Invoice): Single<Status>

    @GET("invoice/")
    fun getInvoiceList(): Single<List<InvoiceDetails>>

    @GET("displayContest/load/{apmId}/{mobileNo}")
    fun loadContestDetails(
        @Path("apmId") apmId: Long,
        @Path("mobileNo") mobileNo: String
    ): Single<DisplayContest>

    @POST("displayContest/createContest")
    fun createDisplayContest(@Body displayContest: DisplayContest):
            Single<Status>

    @GET("pack/selectedProducts/{segment}/{category}")
    fun getSelectedSegmentProducts(
        @Path("segment") segment: String,
        @Path("category") categoryId: Long
    ): Single<List<PackProduct>>

    @GET("pack/vehicleSegment")
    fun getVehicleSegment(): Single<List<String>>

    @POST("coupon/accessory")
    fun sendAccessoryCoupon(@Body couponDataList: CouponData): Single<CouponResponse?>

    @GET("user/upseDetails")
    fun getDetails(): Single<User?>

    @GET("user/upseDetails/{target}")
    fun submitDetails(@Path("target") target: Int): Single<Status?>

    @GET("redemption/pendingApproval/{screen}")
    fun fetchPendingApproval(@Path("screen") screen: String): Single<List<RedemptionOrder>>

    @POST("redemption/pendingApproval/update")
    fun updatePendingApproval(@Body redemptionOrder: RedemptionOrder): Single<Status>

    @GET("user/logoutStatus")
    fun updateLogoutStatus(): Single<Status>

    //Rishta services

    @GET("user/userDetails")
    fun getUser(): Single<VguardRishtaUser>

    @GET("user/userDetails/login")
    fun userLoginDetails(): Single<VguardRishtaUser>

    @POST("user/registerUser")
    fun registerNewUser(@Body vguardRishtaUser: VguardRishtaUser): Single<Status>

    @POST("user/updateKyc")
    fun updateKyc(@Body kycDetails: KycDetails): Single<Status>

    @POST("user/updateKycReatiler")
    fun updateKycReatiler(@Body kycDetails: KycRetailerDetails): Single<Status>

    @POST("product/addToCart")
    fun addToCart(@Body productDetail: ProductDetail): Single<Status>

    @POST("product/removeFromCart")
    fun removeFromCart(@Body productDetail: ProductDetail): Single<Status>

    @POST("order/bankTransfer")
    fun bankTranfer(@Body productOrder: ProductOrder): Single<Status>

    @POST("order/product")
    fun productOrder(@Body productOrder: ProductOrder): Single<Status>

    @POST("order/paytmTransfer")
    fun paytmTransfer(@Body productOrder: ProductOrder): Single<Status>

    @POST("order/paytmTransferAircooler")
    fun paytmTransferForAirCooler(@Body productOrder: ProductOrder): Single<Status>

    @GET("product/getCartProducts")
    fun getCartItems(): Single<List<ProductDetail>>

    @GET("product/getPaytmProductId")
    fun getPaytmPrdouctId(): Single<ProductDetail>

    @GET("product/getBankProductId")
    fun getBankProductId(): Single<ProductDetail>

    @GET("user/shippingAddress")
    fun getShippingAddress(): Single<ShippingAddress>

    @GET("ticket/types")
    fun getTicketTypes(): Single<List<TicketType>>


    @POST("ticket/create")
    fun sendTicket(@Body rt: RaiseTicket): Single<Status>

    @GET("whatsNew/")
    fun getWhatsNew(): Single<List<WhatsNew>>

    @GET("schemes/")
    fun getSchemeImages(): Single<List<SchemeImages>>

    @GET("infoDesk/banners")
    fun getInfoDeskBanners(): Single<List<SchemeImages>>

    @GET("ticket/history")
    fun getTicketHistory(): Single<List<TicketType>>

    @GET("alert/")
    fun getNotifications(): Single<List<Notifications>>

    @GET("user/kycIdTypes")
    fun getKycIdTypes(): Single<List<KycIdTypes>>

    @GET("user/kycIdTypes/{selectedLangId}")
    fun getKycIdTypesByLang(@Path("selectedLangId") selectedLangId: Int): Single<List<KycIdTypes>>


    @POST("user/validateNewMobileNo")
    fun validateNewMobileNo(@Body vru: VguardRishtaUser): Single<Status>

    @POST("user/validateNewUserOtp")
    fun validateOtp(@Body vguardRishtaUser: VguardRishtaUser): Single<Status>

    @GET("user/profile")
    fun getRishtaUserProfile(): Single<VguardRishtaUser>

    @GET("user/bonusPoints")
    fun getBonusRewards(): Single<List<CouponResponse>>

    @GET("product/productWiseOffers")
    fun getProductWiseOffers(): Single<List<ProductWiseOffers>>

    @GET("product/redemptionHistory")
    fun getRedemptionHistory(@Query("type") parameter: String): Single<List<RedemptionHistory>>

    @POST("user/forgotPassword")
    fun forgotPassword(@Body vguardRishtaUser: VguardRishtaUser): Single<Status>

    @GET("coupon/history")
    fun getScanCodeHistory(): Single<List<CouponResponse>>

    @POST("user/generateOtpForLogin")
    fun generateOtpForLogin(@Body vru: VguardRishtaUser): Single<Status>

    @POST("user/generateOtpForReverify")
    fun generateOtpForReverify(@Body vru: VguardRishtaUser): Single<Status>

    @POST("user/validateReverifyOtp")
    fun validateReverifyOtp(@Body vguardRishtaUser: VguardRishtaUser): Single<Status>


    @POST("user/validateLoginOtp")
    fun validateLoginOtp(@Body vguardRishtaUser: VguardRishtaUser): Single<Status>

    @POST("user/updateProfile")
    fun updateProfile(@Body vru: VguardRishtaUser): Single<Status>

    @POST("user/updateBank")
    fun updateBank(@Body bankDetail: BankDetail): Single<Status>

    @GET("product/productWiseOffers/{offerId}")
    fun getProductWiseOffersDetail(@Path("offerId") offerId: String): Single<List<ProductWiseOffersDetail>>

    @GET("product/getProductWiseEarning")
    fun getProdWiseEarning(): Single<List<ProductWiseEarning>>

    @GET("user/getReferralName/{referralCode}")
    fun getReferralName(@Path("referralCode") referralCode: String): Single<VguardRishtaUser>

    @GET("user/monthWiseEarning/{month}/{year}")
    fun getMonthWiseEarning(
        @Path("month") month: String,
        @Path("year") year: String
    ): Single<PointsSummary>

    @GET("coupon/getBonusPoint/{transactionId}")
    fun getBonusPoints(@Path("transactionId") transactionId: String): Single<CouponResponse>

    @GET("user/bankDetails")
    fun getBankDetail(): Single<BankDetail>

    @GET("banks/")
    fun getBanks(): Single<List<BankDetail>>

    @GET("user/kycDetails")
    fun getKycDetails(): Single<KycDetails>

    @POST("user/reUpdateUserForKyc")
    fun reUpdateUserForKyc(@Body vru: VguardRishtaUser): Single<Status>

    @GET("schemes/getSchemeWiseEarning")
    fun getSchemeWiseEarning(): Single<List<SchemeWiseEarning>>

    @GET("user/getProfession/{isService}")
    fun getProfession(@Path("isService") isService: Int): Single<List<Profession>>

    @GET("user/getSubProfession/{professionId}")
    fun getSubProfessions(@Path("professionId") professionId: String): Single<List<Profession>>

    @POST("user/logoutUser")
    fun logoutUser(): Single<Status>

    @GET("state/detailByPincode/{pinCode}")
    fun getDetailsByPinCode(@Path("pinCode") pinCode: String): Single<PincodeDetails>

    @GET("state/citiesByPincodeId/{pinCodeId}")
    fun getCitiesByPincodeId(@Path("pinCodeId") pinCodeId: Long): Single<List<CityMaster>>


    @GET("state/pinCodeList/{pinCode}")
    fun getPincodeList(@Path("pinCode") pinCode: String): Single<List<PincodeDetails>>


    @GET("product/getVguardInfoDownloads")
    fun getVguardInfoDownloads(): Single<List<DownloadData>>


    @GET("product/getVguardProdCatalog")
    fun getVguardProdCatalog(): Single<List<DownloadData>>

    @GET("schemes/getActiveSchemeOffers")
    fun getActiveSchemeOffers(): Single<List<DownloadData>>

    @POST("user/updateLanguageId")
    fun setSelectedLangId(@Body vguardRishtaUser: VguardRishtaUser): Single<Status>

    @GET("alert/count")
    fun getNotificationCount(): Single<Notifications>

    @POST("alert/updateReadCheck")
    fun updateReadCheck(@Body it: Notifications): Single<Status>

    @POST("coupon/processErrorCoupon")
    fun processErrorCoupon(@Body ec: ErrorCoupon): Single<Status>

    @GET("product/retailerCategories")
    fun getRetProductCategories(): Single<List<ProductDetail>>

    @GET("user/getTierDetail")
    fun getTierDetail(): Single<TierPoints>

    @GET("product/retCategoryDealIn")
    fun getRetailerCategoryDealIn(): Single<List<Category>>

    @GET("welfare/")
    fun getWelfarePdfList(): Single<List<DownloadData>>

    @POST("pushNotification/registerToken")
    fun registerToken(@Body vru: VguardRishtaUser): Single<Status>

    @GET("pushNotification/list")
    fun getPushNotifications(): Single<List<Notifications>>

    @GET("product/getCustomerDetails/{mobileNo}")
    fun getCustDetByMobile(@Path("mobileNo") mobileNo: String): Single<CustomerDetailsRegistration?>

    @GET("user/checkRetailerMobile/{mobileNumber}/{category}")
    fun validateMobile(@Path("mobileNumber") mobileNumber: String, @Path("category")dealerCategory: String): Single<MobileValidation>

    @POST("coupon/validateRetailerCoupon")
    fun validateRetailerCoupon(@Body couponData: CouponData): Single<CouponResponse>

    @POST("product/registerCustomer")
    fun sendCustomerData(@Body cdr: CustomerDetailsRegistration): Single<CouponResponse>

    @GET("state/crmState")
    fun getStatesFromCrmApi(): Single<List<StateMaster>>

    @GET("district/crmDistricts/{stateName}")
    fun getDistrictsFromCrmApi(@Path("stateName") stateId: String): Single<List<DistrictMaster>>

    @POST("user/dailyWinners")
    fun getDailyWinner(@Body date: DailyWinner?): Single<List<DailyWinner>>

    @GET("user/getDailyWinnerDates")
    fun getDailyWinnerDates(): Single<List<DailyWinner>>

    @GET("user/getAirCoolerPointsSummary")
    fun getAirCoolerPointsSummary(): Single<PointsSummary>

    @GET("user/getAirCoolerSchemeDetails")
    fun getAirCoolerSchemeDetails(): Single<SchemeDetails>

    @POST("product/registerAirCoolerCustomer")
    fun senAirCoolerData(@Body cdr: CustomerDetailsRegistration): Single<CouponResponse>

    @GET("coupon/airCoolerScanHistory")
    fun getAirCoolerScanCodeHistory(): Single<List<CouponResponse>>

    @GET("state/getCRMStateDistrictByPinCode")
    fun getCRMStateDistrictByPincode(@Query("pinCode") pinCode: String): Single<Triple<String, String,String>>

    @GET("state/getCRMPinCodeList")
    fun getCRMPinCodeList(@Query("pinCode") pinCode: String): Single<List<PincodeDetails>>

    @GET("user/scanPopUp/{id}")
    fun checkScanPopUp(@Path("id") id: String?): Single<MobileValidation>

    @GET("user/getAccessmentYear")
    fun getAy(): Single<List<String>>

    @GET("user/tdsCertificate/{accementYear}")
    fun getTdsList(@Path("accementYear") accementYear: String): Single<List<TdsData>>

    @GET("user/getFiscalYear")
    fun getFiscalYear(): Single<List<String>>

    @GET("user/getMonth")
    fun getMonth():  Single<List<MonthData>>

    @POST("user/getTdsStatementList")
    fun getTdsStatementList(@Body month: MonthData): Single<List<TdsStatement>>

}
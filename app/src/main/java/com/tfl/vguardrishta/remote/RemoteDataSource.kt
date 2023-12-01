package com.tfl.vguardrishta.remote

import android.util.Log
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import io.reactivex.Single
import javax.inject.Inject

open class RemoteDataSource @Inject constructor() {

    private fun service(): ApiService {
        val loginUser = CacheUtils.getUserCreds()
        val encryption = AppUtils.getEncryption()
        Log.d("test", "")
        return ServiceGenerator.createService(
            ApiService::class.java,
            ApiService.prodUrl,
            encryption.decrypt(loginUser.userName),
            encryption.decrypt(loginUser.password)
        )
    }

    fun reLogin(): Single<User> = service().reLogin()

    fun getRedemptionList(filterObject: FilterObject): Single<List<RedemptionOrder>> =
        service().getRedemptionList(filterObject)

    fun authenticateUser(authRequest: String): Single<User> =
        service().authenticateUser(authRequest)

    fun uploadBarcodeList(couponData: CouponData): Single<CouponResponse> =
        service().captureSale(couponData)

    fun sendCouponPin(couponData: CouponData): Single<CouponResponse> =
        service().sendCouponPin(couponData)

    fun getTransactionHistory(filter: FilterObject): Single<List<TransactionHistory>> =
        service().getTransactions(filter)

    fun getProductCategoryList(): Single<List<ProductCategory>> =
        service().getProductCategoryList()

    fun getProductListing(pr: ProductRequest): Single<List<ProductDetail>> =
        service().getProductListing(pr)

    fun getUsers(filter: FilterObject): Single<List<User>> = service().getUsers(filter)

    fun getTerritoryManager(filter: FilterObject): Single<List<User>> =
        service().getTerritoryManager(filter)

    fun sendUser(user: User) = service().sendUser(user)

    fun updateBankDetails(user: User) = service().updateBankDetails(user)

    fun getStates(): Single<List<StateMaster>> = service().getStates()

    fun getDistricts(stateId: Long): Single<List<DistrictMaster>> = service().getDistricts(stateId)

    fun getDistrictMaster(stateId: Long): Single<List<DistrictMaster>> =
        service().getDistrictMaster(stateId)

    fun getCities(districtId: Long): Single<List<CityMaster>> = service().getCities(districtId)

    fun checkMobileNumber(user: User): Single<Status> = service().checkMobileNumber(user)

    fun generateOtp(user: User): Single<Status> = service().generateOtp(user)

    fun verifyOTP(user: User) = service().verifyOTP(user)

    fun redeem(redemption: Redemption) = service().redeem(redemption)

    fun getUserByMobile(mobileNo: String): Single<SPCoupon> = service().getUserByMobile(mobileNo)

    fun uploadOrder(order: Order): Single<Status> = service().uploadOrder(order)

    fun getOrders(): Single<List<Order>> = service().getOrders()

    fun searchCoupon(string: String): Single<SearchCoupon> = service().searchCoupon(string)

    fun getCategoryList(): Single<List<Category>> = service().getCategoryList()

    fun getDownloads(string: String): Single<List<DownloadData>> = service().getDownloads()

    fun getAppVersion(): Single<String> = service().getAppVersion()

    fun updateUser(user: User) = service().updateUser(user)

    fun getPackCategoryList() = service().getPackCategoryList()

    fun getPackProductListing(categoryId: Long) = service().getPackProductListing(categoryId)

    fun updateToken(token: String) = service().updateToken(token)

    fun getCouponList(): Single<List<RedemptionCouponMaster>> = service().getCouponList()

    fun changePassword(changePassword: ChangePassword) = service().changePassword(changePassword)

    fun getDistList(): Single<List<User>> = service().getDistributorList()

    fun getByIfsc(ifscCode: String): Single<BankMaster?> = service().getByIfsc(ifscCode)

    fun updateOrder(long: Long): Single<Status> = service().updateOrder(long)

    fun uploadInvoice(invoice: Invoice): Single<Status> = service().uploadInvoice(invoice)

    fun getInvoiceList(): Single<List<InvoiceDetails>> = service().getInvoiceList()

    fun loadContestDetails(apmId: Long, mobileNo: String): Single<DisplayContest> =
        service().loadContestDetails(apmId, mobileNo)

    fun createDisplayContest(displayContest: DisplayContest): Single<Status> =
        service().createDisplayContest(displayContest)

    fun getSelectedSegmentProducts(segment: String, category: Long): Single<List<PackProduct>> =
        service().getSelectedSegmentProducts(segment, category)

    fun getVehicleSegment(): Single<List<String>> = service().getVehicleSegment()

    fun sendAccessoryCoupon(couponData: CouponData): Single<CouponResponse?> =
        service().sendAccessoryCoupon(couponData)

    fun getDetails(): Single<User?> = service().getDetails()

    fun submitDetails(target: Int): Single<Status?> = service().submitDetails(target)

    fun fetchPendingApproval(screen: String): Single<List<RedemptionOrder>> =
        service().fetchPendingApproval(screen)

    fun updatePendingApproval(redemptionOrder: RedemptionOrder): Single<Status> =
        service().updatePendingApproval(redemptionOrder)

    fun updateLogoutStatus(): Single<Status> = service().updateLogoutStatus()
    fun getUser(): Single<VguardRishtaUser> {
        return service().getUser()
    }

    fun userLoginDetails(): Single<VguardRishtaUser> {
        return service().userLoginDetails()
    }


    fun registerNewUser(vguardRishtaUser: VguardRishtaUser): Single<Status> {
        return service().registerNewUser(vguardRishtaUser)
    }

    fun updateKyc(kycDetails: KycDetails): Single<Status> {
        return service().updateKyc(kycDetails)
    }

    fun updateKycReatiler(kycDetails: KycRetailerDetails): Single<Status> {
        return service().updateKycReatiler(kycDetails)
    }

    fun addToCart(productDetail: ProductDetail): Single<Status> {
        return service().addToCart(productDetail)
    }

    fun bankTransfer(po: ProductOrder): Single<Status> {
        return service().bankTranfer(po)
    }

    fun paytmOrder(po: ProductOrder): Single<Status> {
        return service().paytmTransfer(po)
    }

    fun paytmOrderForAirCooler(po: ProductOrder): Single<Status> {
        return service().paytmTransferForAirCooler(po)
    }



    fun removeFromCart(productDetail: ProductDetail): Single<Status> {
        return service().removeFromCart(productDetail)
    }

    fun orderProduct(po: ProductOrder): Single<Status> {
        return service().productOrder(po)
    }

    fun getCartItems(): Single<List<ProductDetail>> {
        return service().getCartItems()
    }

    fun getPaytmProductId(): Single<ProductDetail> {
        return service().getPaytmPrdouctId()
    }

    fun getBankProductId(): Single<ProductDetail> {
        return service().getBankProductId()
    }

    fun getShippingAddress(): Single<ShippingAddress> {
        return service().getShippingAddress()
    }

    fun getTicketTypes(): Single<List<TicketType>> {
        return service().getTicketTypes()
    }

    fun sendTicket(rt: RaiseTicket): Single<Status> {
        return service().sendTicket(rt)
    }

    fun getWhatsNew(): Single<List<WhatsNew>> {
        return service().getWhatsNew()
    }

    fun getSchemeImages(): Single<List<SchemeImages>> {
        return service().getSchemeImages()
    }

    fun getTicektHistory(): Single<List<TicketType>> {
        return service().getTicketHistory()
    }

    fun getNotifications(): Single<List<Notifications>> {
        return service().getNotifications()
    }

    fun getKycIdTypes(): Single<List<KycIdTypes>> {
        return service().getKycIdTypes()
    }

    fun getKycIdTypesByLang(selectedLangId: Int): Single<List<KycIdTypes>> {
        return service().getKycIdTypesByLang(selectedLangId)
    }


    fun validateNewMobileNo(vru: VguardRishtaUser): Single<Status> {
        return service().validateNewMobileNo(vru)
    }

    fun validateOtp(vguardRishtaUser: VguardRishtaUser): Single<Status> {
        return service().validateOtp(vguardRishtaUser)
    }

    fun getRishtaUserProfile(): Single<VguardRishtaUser> {
        return service().getRishtaUserProfile()
    }

    fun getBonusRewards(): Single<List<CouponResponse>> {
        return service().getBonusRewards()
    }

    fun getProductWiseOffers(): Single<List<ProductWiseOffers>> {
        return service().getProductWiseOffers()
    }

    fun getRedemptionHistory(parameters: String): Single<List<RedemptionHistory>> {
        return service().getRedemptionHistory(parameters)
    }

    fun forgotPassword(user: VguardRishtaUser): Single<Status> {
        return service().forgotPassword(user)
    }

    fun getScanCodeHistory(): Single<List<CouponResponse>> {
        return service().getScanCodeHistory()
    }

    fun generateOtpForLogin(vru: VguardRishtaUser): Single<Status> {
        return service().generateOtpForLogin(vru)
    }

    fun validateLoginOtp(vguardRishtaUser: VguardRishtaUser): Single<Status> {
        return service().validateLoginOtp(vguardRishtaUser)
    }

    fun updateProfile(vru: VguardRishtaUser): Single<Status> {
        return service().updateProfile(vru)
    }

    fun updateBank(bankDetail: BankDetail): Single<Status> {
        return service().updateBank(bankDetail)
    }

    fun getProductWiseOffersDetail(offerId: String): Single<List<ProductWiseOffersDetail>> {
        return service().getProductWiseOffersDetail(offerId)
    }

    fun getProdWiseEarning(): Single<List<ProductWiseEarning>> {
        return service().getProdWiseEarning()
    }

    fun getInfoDeskBanners(): Single<List<SchemeImages>> {
        return service().getInfoDeskBanners()
    }

    fun getReferralName(code: String): Single<VguardRishtaUser> {
        return service().getReferralName(code)
    }

    fun getMonthWiseEarning(parameters: Pair<String, String>): Single<PointsSummary> {
        return service().getMonthWiseEarning(parameters.first, parameters.second)
    }

    fun getBonusPoints(transactId: String): Single<CouponResponse> {
        return service().getBonusPoints(transactId)
    }

    fun getBankDetail(): Single<BankDetail> {
        return service().getBankDetail()
    }

    fun getBanks(): Single<List<BankDetail>> {
        return service().getBanks()
    }

    fun getKycDetails(): Single<KycDetails> {
        return service().getKycDetails()
    }

    fun getSchemeWiseEarning(): Single<List<SchemeWiseEarning>> {
        return service().getSchemeWiseEarning()
    }

    fun getProfession(isService: Int): Single<List<Profession>> {
        return service().getProfession(isService)
    }

    fun getSubProfessions(professionId: String): Single<List<Profession>> {
        return service().getSubProfessions(professionId)
    }

    fun logoutUser(): Single<Status> {
        return service().logoutUser()
    }

    fun getDetailsByPinCode(pinCode: String): Single<PincodeDetails> {
        return service().getDetailsByPinCode(pinCode)
    }

    fun getPincodeList(pinCode: String): Single<List<PincodeDetails>> {
        return service().getPincodeList(pinCode)
    }

    fun getVguardInfoDownloads(): Single<List<DownloadData>> {
        return service().getVguardInfoDownloads()
    }

    fun getVguardCatlogDownloads(): Single<List<DownloadData>> {
        return service().getVguardProdCatalog()
    }

    fun getActiveSchemeOffers(): Single<List<DownloadData>> {
        return service().getActiveSchemeOffers()
    }

    fun setSelectedLangId(vguardRishtaUser: VguardRishtaUser): Single<Status> {
        return service().setSelectedLangId(vguardRishtaUser)
    }

    fun getNotificationCount(): Single<Notifications> {
        return service().getNotificationCount()
    }

    fun updateReadCheck(it: Notifications): Single<Status> {
        return service().updateReadCheck(it)
    }

    fun getStateDistCitiesByPincodeId(pinCodeId: String): Single<List<CityMaster>> {
        return service().getCitiesByPincodeId(pinCodeId.toLong())
    }

    fun generateOtpForReverify(parameters: VguardRishtaUser): Single<Status> {
        return service().generateOtpForReverify(parameters)
    }

    fun validateReverifyOtp(vguardRishtaUser: VguardRishtaUser): Single<Status> {
        return service().validateReverifyOtp(vguardRishtaUser)
    }

    fun reUpdateUserForKyc(vru: VguardRishtaUser): Single<Status> {
        return service().reUpdateUserForKyc(vru)
    }

    fun processErrorCoupon(ec: ErrorCoupon): Single<Status> {
        return service().processErrorCoupon(ec)
    }

    fun getRetProductCategories(): Single<List<ProductDetail>> {

        return service().getRetProductCategories()
    }

    fun getTierDetail(): Single<TierPoints> {
        return service().getTierDetail()
    }

    fun getRetailerCategoryDealIn(): Single<List<Category>> {
        return service().getRetailerCategoryDealIn()
    }

    fun getWelfarePdfList(): Single<List<DownloadData>> {
        return service().getWelfarePdfList()
    }

    fun  getTdsList(accessmentYear : String): Single<List<TdsData>> {
        return service().getTdsList(accessmentYear)
    }


    fun updateFcmToken(vru: VguardRishtaUser): Single<Status> {
        return service().registerToken(vru)
    }

    fun getPushNotifications(): Single<List<Notifications>> {

        return service()?.getPushNotifications()
    }

    fun getCustDetByMobile(mobileNo: String): Single<CustomerDetailsRegistration?> {
        return service().getCustDetByMobile(mobileNo)
    }

    fun validateMobile(mobileNo: String, dealerCategory: String): Single<MobileValidation> {
        return service().validateMobile(mobileNo,dealerCategory)
    }

    fun validateRetailerCoupon(couponData: CouponData): Single<CouponResponse> {

        return service().validateRetailerCoupon(couponData)
    }

    fun sendCustomerData(cdr: CustomerDetailsRegistration): Single<CouponResponse> {
        return service().sendCustomerData(cdr)
    }

    fun getStatesFromCrmApi(): Single<List<StateMaster>> {
        return service().getStatesFromCrmApi()
    }

    fun getDistrictsFromCrmApi(stateName: String): Single<List<DistrictMaster>> {
        return service().getDistrictsFromCrmApi(stateName)
    }

    fun getDailyWinner(date: DailyWinner?): Single<List<DailyWinner>> {
        return service().getDailyWinner(date)
    }

    fun getDailyWinnerDates(): Single<List<DailyWinner>> {
        return service().getDailyWinnerDates()
    }

    fun getAirCoolerPointsSummary() = service().getAirCoolerPointsSummary()
    fun getAirCoolerSchemeDetails() = service().getAirCoolerSchemeDetails()
    fun senAirCoolerData(cdr: CustomerDetailsRegistration) = service().senAirCoolerData(cdr)
    fun getAirCoolerScanCodeHistory() = service().getAirCoolerScanCodeHistory()
    fun getCRMStateDistrictByPincode(pinCode: String) = service().getCRMStateDistrictByPincode(pinCode)
    fun getCRMPinCodeList(pinCode: String)= service().getCRMPinCodeList(pinCode)

    fun checkScanPopUp(userCode: String?): Single<MobileValidation> {
        return service().checkScanPopUp(userCode)
    }

    fun getAy(): Single<List<String>> = service().getAy()
    fun getFiscalYear()=service().getFiscalYear()
    fun getMonth()=service().getMonth()
    fun getTdsStatementList(month: MonthData)=service().getTdsStatementList(month)

}
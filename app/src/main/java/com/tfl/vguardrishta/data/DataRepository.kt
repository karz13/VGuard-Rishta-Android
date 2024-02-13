package com.tfl.vguardrishta.data

import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.remote.RemoteDataSource
import io.reactivex.Single
import javax.inject.Inject


class DataRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    fun reLogin() = remoteDataSource.reLogin()

    fun getRedemptionList(filter: FilterObject) = remoteDataSource.getRedemptionList(filter)

    fun getTransactionHistory(filter: FilterObject) = remoteDataSource.getTransactionHistory(filter)

    fun authenticateUser(authRequest: String) = remoteDataSource.authenticateUser(authRequest)

    fun uploadBarcodeList(couponDataList: CouponData) =
        remoteDataSource.uploadBarcodeList(couponDataList)

    fun sendCouponPin(couponDataList: CouponData) =
        remoteDataSource.sendCouponPin(couponDataList)

    fun getProductCategoryList() = remoteDataSource.getProductCategoryList()
    fun productgenerateOtp(otp:OTP):  Single<Status> = remoteDataSource.productgenerateOtp(otp)

    fun getProductListing(pr: ProductRequest) = remoteDataSource.getProductListing(pr)

    fun getUsers(filter: FilterObject) = remoteDataSource.getUsers(filter)

    fun getTerritoryManager(filter: FilterObject) = remoteDataSource.getTerritoryManager(filter)

    fun sendUser(user: User) = remoteDataSource.sendUser(user)

    fun updateBankDetails(user: User) = remoteDataSource.updateBankDetails(user)

    fun getStates() = remoteDataSource.getStates()

    fun getDistricts(stateId: Long) = remoteDataSource.getDistricts(stateId)

    fun getDistrictMaster(stateId: Long) = remoteDataSource.getDistrictMaster(stateId)

    fun getCities(districtId: Long) = remoteDataSource.getCities(districtId)

    fun checkMobileNumber(user: User) = remoteDataSource.checkMobileNumber(user)

    fun generateOtp(user: User) = remoteDataSource.generateOtp(user)

    fun verifyOTP(user: User) = remoteDataSource.verifyOTP(user)

    fun redeem(redemption: Redemption) = remoteDataSource.redeem(redemption)

    fun getUserByMobile(parameters: String): Single<SPCoupon> =
        remoteDataSource.getUserByMobile(parameters)

    fun uploadOrder(order: Order) = remoteDataSource.uploadOrder(order)

    fun getOrders() = remoteDataSource.getOrders()

    fun searchCoupon(string: String) = remoteDataSource.searchCoupon(string)

    fun getCategoryList() = remoteDataSource.getCategoryList()

    fun getDownloads(string: String) = remoteDataSource.getDownloads(string)

    fun getAppVersion() = remoteDataSource.getAppVersion()

    fun updateUser(user: User) = remoteDataSource.updateUser(user)


    fun getPackCategoryList() = remoteDataSource.getPackCategoryList()

    fun getPackProductListing(categoryId: Long) = remoteDataSource.getPackProductListing(categoryId)

    fun updateOrder(long: Long) = remoteDataSource.updateOrder(long)

    fun getCouponList() = remoteDataSource.getCouponList()

    fun changePassword(changePassword: ChangePassword) =
        remoteDataSource.changePassword(changePassword)

    fun getDistList() = remoteDataSource.getDistList()

    fun getByIfsc(ifscCode: String): Single<BankMaster?> = remoteDataSource.getByIfsc(ifscCode)

    fun uploadInvoice(invoice: Invoice) = remoteDataSource.uploadInvoice(invoice)

    fun getInvoiceList() = remoteDataSource.getInvoiceList()

    fun loadContestDetails(apmId: Long, mobileNo: String) =
        remoteDataSource.loadContestDetails(apmId, mobileNo)

    fun createDisplayContest(displayContest: DisplayContest) =
        remoteDataSource.createDisplayContest(displayContest)

    fun getSelectedProductList(segments: String, category: Long): Single<List<PackProduct>> =
        remoteDataSource.getSelectedSegmentProducts(segments, category)
    fun getSpecialOffers(): Single<List<SpecialSchemes>> {
        return remoteDataSource.getSpecialOffers()
    }
    fun getVehicleSegment(): Single<List<String>> = remoteDataSource.getVehicleSegment()

    fun sendAccessoryCoupon(couponData: CouponData) =
        remoteDataSource.sendAccessoryCoupon(couponData)

    fun getDetails(): Single<User?> = remoteDataSource.getDetails()

    fun submitDetails(target: Int): Single<Status?> = remoteDataSource.submitDetails(target)

    fun fetchPendingApproval(screen: String): Single<List<RedemptionOrder>> =
        remoteDataSource.fetchPendingApproval(screen)

    fun updatePendingApproval(redemptionOrder: RedemptionOrder): Single<Status> =
        remoteDataSource.updatePendingApproval(redemptionOrder)

    fun updateLogoutStatus(): Single<Status> = remoteDataSource.updateLogoutStatus()
    fun getUser(): Single<VguardRishtaUser> {
        return remoteDataSource.getUser()
    }

    fun userLoginDetails(): Single<VguardRishtaUser> {
        return remoteDataSource.userLoginDetails()
    }

    fun registerNewUser(vguardRishtaUser: VguardRishtaUser): Single<Status> {
        return remoteDataSource.registerNewUser(vguardRishtaUser)
    }

    fun updateKyc(kycDetails: KycDetails): Single<Status> {
        return remoteDataSource.updateKyc(kycDetails)
    }

    fun updateKycRetailer(kycDetails: KycRetailerDetails): Single<Status> {
        return remoteDataSource.updateKycReatiler(kycDetails)
    }


    fun addToCart(productDetail: ProductDetail): Single<Status> {
        return remoteDataSource.addToCart(productDetail)
    }

    fun bankTransfer(po: ProductOrder): Single<Status> {
        return remoteDataSource.bankTransfer(po)
    }

    fun paytmOrder(po: ProductOrder): Single<Status> {

        return remoteDataSource.paytmOrder(po)
    }
    fun paytmOrderForAirCooler(po: ProductOrder): Single<Status> {

        return remoteDataSource.paytmOrderForAirCooler(po)
    }

    fun removeFromCart(productDetail: ProductDetail): Single<Status> {
        return remoteDataSource.removeFromCart(productDetail)
    }

    fun orderProduct(po: ProductOrder): Single<Status> {
        return remoteDataSource.orderProduct(po)
    }

    fun getCartItems(): Single<List<ProductDetail>> {

        return remoteDataSource.getCartItems()
    }

    fun getPaytmProductId(): Single<ProductDetail> {
        return remoteDataSource.getPaytmProductId()
    }

    fun getBankProductId(): Single<ProductDetail> {
        return remoteDataSource.getBankProductId()
    }

    fun getShippingAddress(): Single<ShippingAddress> {

        return remoteDataSource.getShippingAddress()
    }

    fun getTicketTypes(): Single<List<TicketType>> {
        return remoteDataSource.getTicketTypes()
    }

    fun sendTicket(rt: RaiseTicket): Single<Status> {
        return remoteDataSource.sendTicket(rt)
    }

    fun getWhatsNew(): Single<List<WhatsNew>> {
        return remoteDataSource.getWhatsNew()
    }

    fun getSchemeImages(): Single<List<SchemeImages>> {
        return remoteDataSource.getSchemeImages()
    }

    fun getTicketHistory(): Single<List<TicketType>> {
        return remoteDataSource.getTicektHistory()
    }

    fun getNotifications(): Single<List<Notifications>> {
        return remoteDataSource.getNotifications()
    }

    fun getKycIdTypes(): Single<List<KycIdTypes>> {
        return remoteDataSource.getKycIdTypes()
    }

    fun getKycIdTypesByLang(selectedLangId: Int): Single<List<KycIdTypes>> {
        return remoteDataSource.getKycIdTypesByLang(selectedLangId)
    }

    fun validateNewMobileNo(vru: VguardRishtaUser): Single<Status> {
        return remoteDataSource.validateNewMobileNo(vru)
    }

    fun validateOtp(vguardRishtaUser: VguardRishtaUser): Single<Status> {
        return remoteDataSource.validateOtp(vguardRishtaUser)
    }

    fun getRishtaUserProfile(): Single<VguardRishtaUser> {
        return remoteDataSource.getRishtaUserProfile()
    }

    fun getBonusRewards(): Single<List<CouponResponse>> {
        return remoteDataSource.getBonusRewards()
    }

    fun getProductWiseOffers(): Single<List<ProductWiseOffers>> {

        return remoteDataSource.getProductWiseOffers()
    }

    fun getRedemptionHistory(parameters:String): Single<List<RedemptionHistory>> {
        return remoteDataSource.getRedemptionHistory(parameters)
    }

    fun forgotPassword(user: VguardRishtaUser): Single<Status> {
        return remoteDataSource.forgotPassword(user)
    }

    fun getScanCodeHistory(): Single<List<CouponResponse>> {
        return remoteDataSource.getScanCodeHistory()
    }

    fun generateOtpForLogin(vru: VguardRishtaUser): Single<Status> {
        return remoteDataSource.generateOtpForLogin(vru)
    }

    fun validateLoginOtp(vguardRishtaUser: VguardRishtaUser): Single<Status> {
        return remoteDataSource.validateLoginOtp(vguardRishtaUser)
    }

    fun updateProfile(vru: VguardRishtaUser): Single<Status> {
        return remoteDataSource.updateProfile(vru)
    }

    fun updateBank(bankDetail: BankDetail): Single<Status> {
        return remoteDataSource.updateBank(bankDetail)
    }

    fun getProductWiseOffersDetail(offerId: String): Single<List<ProductWiseOffersDetail>> {
        return remoteDataSource.getProductWiseOffersDetail(offerId)
    }

    fun getProdWiseEarning(): Single<List<ProductWiseEarning>> {
        return remoteDataSource.getProdWiseEarning()
    }

    fun getInfoDeskBanners(): Single<List<SchemeImages>> {
        return remoteDataSource.getInfoDeskBanners()
    }

    fun getReferralName(code: String): Single<VguardRishtaUser> {
        return remoteDataSource.getReferralName(code)
    }

    fun getMonthWiseEarning(parameters: Pair<String, String>): Single<PointsSummary> {

        return remoteDataSource.getMonthWiseEarning(parameters)
    }

    fun getBonusPoints(transactId: String): Single<CouponResponse> {
        return remoteDataSource.getBonusPoints(transactId)
    }

    fun getBankDetail(): Single<BankDetail> {
        return remoteDataSource.getBankDetail()
    }

    fun getBanks(): Single<List<BankDetail>> {
        return remoteDataSource.getBanks()
    }

    fun getKycDetails(): Single<KycDetails> {
        return remoteDataSource.getKycDetails()
    }

    fun getSchemeWiseEarning(): Single<List<SchemeWiseEarning>> {
        return remoteDataSource.getSchemeWiseEarning()
    }

    fun getProfession(isService: Int): Single<List<Profession>> {
        return remoteDataSource.getProfession(isService)
    }

    fun getSubProfessions(professionId: String): Single<List<Profession>> {
        return remoteDataSource.getSubProfessions(professionId)
    }

    fun logoutUser(): Single<Status> {
        return remoteDataSource.logoutUser()
    }

    fun getDetailsByPinCode(pinCode: String): Single<PincodeDetails> {
        return remoteDataSource.getDetailsByPinCode(pinCode)
    }

    fun getPincodeList(pinCode: String): Single<List<PincodeDetails>> {
        return remoteDataSource.getPincodeList(pinCode)
    }

    fun getVguardInfoDownloads(): Single<List<DownloadData>> {
        return remoteDataSource.getVguardInfoDownloads()
    }

    fun getVguardCatlogDownloads(): Single<List<DownloadData>> {
        return remoteDataSource.getVguardCatlogDownloads()
    }

    fun getActiveSchemeOffers(): Single<List<DownloadData>> {
        return remoteDataSource.getActiveSchemeOffers()
    }

    fun setSelectedLangId(vguardRishtaUser: VguardRishtaUser): Single<Status> {
        return remoteDataSource.setSelectedLangId(vguardRishtaUser)
    }

    fun getNotificationCount(): Single<Notifications> {
        return remoteDataSource.getNotificationCount()
    }

    fun updateReadCheck(it: Notifications): Single<Status> {
        return remoteDataSource.updateReadCheck(it)
    }

    fun getStateDistCitiesByPincodeId(pinCodeId: String): Single<List<CityMaster>> {
        return remoteDataSource.getStateDistCitiesByPincodeId(pinCodeId)
    }

    fun generateOtpForReverify(parameters: VguardRishtaUser): Single<Status> {
        return remoteDataSource.generateOtpForReverify(parameters)
    }

    fun validateReverifyOtp(vguardRishtaUser: VguardRishtaUser): Single<Status> {
        return remoteDataSource.validateReverifyOtp(vguardRishtaUser)
    }

    fun reUpdateUserForKyc(vru: VguardRishtaUser): Single<Status> {
        return remoteDataSource.reUpdateUserForKyc(vru)
    }

    fun processErrorCoupon(ec: ErrorCoupon): Single<Status> {

        return remoteDataSource.processErrorCoupon(ec)
    }

    fun getWelfarePdfList(): Single<List<DownloadData>> {

        return remoteDataSource.getWelfarePdfList()
    }

    fun getTdsList(accessmentYear : String): Single<List<TdsData>> {

        return remoteDataSource.getTdsList(accessmentYear)
    }

    fun updateToken(token: String) = remoteDataSource.updateToken(token)
    fun updateFcmToken(vru: VguardRishtaUser): Single<Status> {
        return remoteDataSource.updateFcmToken(vru)
    }

    fun getPushNotifications(): Single<List<Notifications>> {

        return remoteDataSource.getPushNotifications()
    }

    fun validateRetailerCoupon(couponData: CouponData): Single<CouponResponse> {
        return remoteDataSource.validateRetailerCoupon(couponData)
    }

    fun getAirCoolerPointsSummary() = remoteDataSource.getAirCoolerPointsSummary()
    fun getAirCoolerSchemeDetails() = remoteDataSource.getAirCoolerSchemeDetails()
    fun getAirCoolerScanCodeHistory() = remoteDataSource.getAirCoolerScanCodeHistory()


    fun checkScanPopUp(userCode: String?): Single<MobileValidation> {
        return remoteDataSource.checkScanPopUp(userCode)
    }

    fun getAy() = remoteDataSource.getAy()

    fun getFiscalYear()=remoteDataSource.getFiscalYear()
    fun getMonth()=remoteDataSource.getMonth()
    fun getTdsStatementList(month: MonthData)=remoteDataSource.getTdsStatementList(month)

    fun registerWarranty(cdr: CustomerDetailsRegistration): Single<CouponResponse> {
        return remoteDataSource.registerWarranty(cdr)
    }
}
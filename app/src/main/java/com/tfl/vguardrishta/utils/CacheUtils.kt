package com.tfl.vguardrishta.utils

import android.content.Context
import android.net.Uri
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.models.*
import io.paperdb.Paper
import kotlin.jvm.internal.Intrinsics


/**
 * Created by Shanmuka on 4/23/2019.
 */
object CacheUtils {

    private var retSelectedProdForScan = ProductDetail()
    private var bankList: ArrayList<BankDetail> = arrayListOf()
    private var userType: String = ""
    private lateinit var newUser: User
    private lateinit var pd: ProductDetail
    private var refreshPointView = true
    private var newVguardRishtaUser =
        VguardRishtaUser()
    private var customerDetailsRegistration = CustomerDetailsRegistration()
    private var shopPhotoUri: Uri? = null
    private var chequePhotoUri: Uri? = null
    private lateinit var fileUploader: FileUploader
    private lateinit var products: ArrayList<ProductDetail>
    private lateinit var productCategoryList: ArrayList<ProductCategory>
    private lateinit var packCategoryList: ArrayList<PackCategory>
    private lateinit var filteredUserList: List<User>
    private var vehicleSegmentList: List<String> = arrayListOf()
    private var airCoolerProduct: ProductDetail? = null

    fun setAirCoolerProduct(airCoolerProduct: ProductDetail?) {
        this.airCoolerProduct = airCoolerProduct
    }

    fun getAirCoolerProduct() = airCoolerProduct

    fun writeUserCreds(user: UserCreds?) {
        Paper.book().write(Constants.KEY_USER_CREDS, user)
    }

    fun getUserCreds(): UserCreds =
        Paper.book().read<UserCreds>(Constants.KEY_USER_CREDS) ?: UserCreds()

    fun refreshView(b: Boolean) {
        refreshPointView = b
    }
    fun setSchemeProgress(data: SchemeProgressData) {

        Paper.book().write("SCHEMEPROGRESS", data)
    }

    fun getSchemeProgress(): SchemeProgressData =
        Paper.book().read<SchemeProgressData>("SCHEMEPROGRESS")?:SchemeProgressData()



    fun getRefreshView(): Boolean {
        return refreshPointView
    }

    fun writeIsUserRefreshReq(boolean: Boolean) {
        Paper.book().write(Constants.KEY_USER_REFRESH_REQ, boolean)
    }

    fun getIsUserRefreshReq(): Boolean =
        Paper.book().read<Boolean>(Constants.KEY_USER_REFRESH_REQ) ?: false

    fun setUserType(string: String?) {
        userType = string!!
    }

    fun getUserType(): String = userType

    fun setRishtaUserType(string: String?) {
        Paper.book().write("userType", string)
    }

    fun setSubUserType(string: String?) {
        Paper.book().write("subUserType", string)
    }

    fun getRishtaUserType(): String = Paper.book().read<String>("userType") ?: "1"

    fun getSubUserType(): String = Paper.book().read<String>("subUserType") ?: "1"


    fun setUser(user: User) {
        if (getUserType().isNotEmpty()) {
            user.userType = getUserType()
        }
        newUser = user
    }

    fun saveVguardUser(rishtaUser: VguardRishtaUser) {
        Paper.book().write(Constants.KEY_RISHTA_USER, rishtaUser)
    }

    fun getRishtaUser(): VguardRishtaUser =
        Paper.book().read<VguardRishtaUser>(Constants.KEY_RISHTA_USER) ?: VguardRishtaUser()


    fun setNewRishtaUser(rishtaUser: VguardRishtaUser) {
        this.newVguardRishtaUser = rishtaUser
    }

    fun getNewRishtaUser(): VguardRishtaUser {
        return newVguardRishtaUser
    }

    fun setCustomerDetReg(cdr: CustomerDetailsRegistration) {
        this.customerDetailsRegistration = cdr
    }

    fun getCustomerDetReg(): CustomerDetailsRegistration {
        return this.customerDetailsRegistration
    }

    fun clearCustomerDetReg() {
        this.customerDetailsRegistration = CustomerDetailsRegistration()
    }

    fun clearUserDetails() {
        Paper.book().delete(Constants.KEY_USER)
        Paper.book().delete(Constants.KEY_RISHTA_USER)
        Paper.book().delete(Constants.KEY_USER_CREDS)

        newUser = User()
        newVguardRishtaUser = VguardRishtaUser()
        setShopUri(null)
        setChequePhotoUri(null)
        setStates(arrayListOf())
        setDistricts(arrayListOf())
        setCities(arrayListOf())
        setStatePosition(0)
        setDistrictPosition(0)
        setCityPosition(0)
        setSegments(arrayListOf())
        setCustomerSegments(arrayListOf())
        setFileUploader(FileUploader())
        clearCustomerDetReg()
    }

    fun getUser(): User {
        return if (::newUser.isInitialized) {
            if (getUserType().isNotEmpty()) {
                newUser.userType = getUserType()
            }
            newUser
        } else User()
    }

    fun getFileUploader(): FileUploader =
        if (::fileUploader.isInitialized) fileUploader else FileUploader()

    fun setFileUploader(userFileUploader: FileUploader) {
        fileUploader = userFileUploader
    }

    fun getLoginUser(): User = Paper.book().read<User>(Constants.KEY_USER) ?: User()

    fun getLoginUserType(): String? {
        val loginUser = getLoginUser()
        return loginUser.userType
    }

    fun setShopUri(photoUri: Uri?) {
        shopPhotoUri = photoUri
    }

    fun setChequePhotoUri(photoUri: Uri?) {
        chequePhotoUri = photoUri
    }

    fun getChequePhotoUri(): Uri? = chequePhotoUri

    fun getFirstState(context: Context): ArrayList<StateMaster> {
        val arrayList = ArrayList<StateMaster>()
        val state = StateMaster()
        state.stateName = context.getString(R.string.select_state)
        state.id = -1
        arrayList.add(state)
        return arrayList
    }

    fun getFirstProfession(context: Context): ArrayList<Profession> {
        val arrayList = ArrayList<Profession>()
        val profession = Profession()
        profession.professionName = context.getString(R.string.select_profession)
        profession.professionId = -1
        arrayList.add(profession)
        return arrayList
    }

    fun getFirstSubProfession(context: Context): ArrayList<Profession> {
        val arrayList = ArrayList<Profession>()
        val profession = Profession()
        profession.professionName = context.getString(R.string.select_sub_profession_category)
        profession.professionId = -1
        arrayList.add(profession)
        return arrayList
    }

    fun getFirstBank(context: Context): ArrayList<BankDetail> {
        val arrayList = ArrayList<BankDetail>()
        val state = BankDetail()
        state.bankNameAndBranch = context.getString(R.string.select_bank)
        state.bankId = -1
        arrayList.add(state)
        return arrayList
    }

    fun getFirstWinnerDate(context: Context): ArrayList<DailyWinner> {
        val arrayList = ArrayList<DailyWinner>()
        val dw = DailyWinner()
        dw.date = context.getString(R.string.date)
        arrayList.add(dw)
        return arrayList
    }

    fun setBanks(list: ArrayList<BankDetail>) {
        bankList = list
    }

    fun getBanks(): ArrayList<BankDetail> = if (bankList.isNotEmpty()) bankList else arrayListOf()


    fun getFirstKycTypes(context: Context): ArrayList<KycIdTypes> {
        val arrayList = ArrayList<KycIdTypes>()
        val state = KycIdTypes()
        state.kycIdName = context.getString(R.string.select_kyc_type)
        state.kycId = -1
        arrayList.add(state)
        return arrayList
    }

    fun getFirstDistrict(context: Context): ArrayList<DistrictMaster> {
        val arrayList = ArrayList<DistrictMaster>()
        val district = DistrictMaster()
        district.districtName = context.getString(R.string.select_district)
        district.id = -1
        arrayList.add(district)
        return arrayList
    }

    fun getFirstCity(context: Context): ArrayList<CityMaster> {
        val arrayList = ArrayList<CityMaster>()
        val city = CityMaster()
        city.cityName = context.getString(R.string.select_city)
        city.id = -1L
        arrayList.add(city)
        val city2 = CityMaster()
        city2.cityName = context.getString(R.string.other)
        city2.id = -2L
        arrayList.add(city2)
        return arrayList
    }

    fun getFirstUser(context: Context): ArrayList<User> {
        val arrayList = ArrayList<User>()
        val user = User()
        user.username = context.getString(R.string.select)
        user.companyName = context.getString(R.string.select)
        user.id = -1
        arrayList.add(user)
        return arrayList
    }

    fun getFirstUserAsAll(context: Context): ArrayList<User> {
        val arrayList = ArrayList<User>()
        val user = User()
        user.username = context.getString(R.string.all)
        arrayList.add(user)
        return arrayList
    }

    fun getFirstCategory(context: Context): ArrayList<Category> {
        val arrayList = ArrayList<Category>()
        val category = Category()
        category.prodCatName = context.getString(R.string.all_category)
        category.prodCatId = -1L
        arrayList.add(category)
        return arrayList
    }

    fun getFirstRetailerCategoryDealIn(context: Context): ArrayList<Category> {
        val arrayList = ArrayList<Category>()
        val category = Category()
        category.prodCatName = context.getString(R.string.select)
        category.prodCatId = -1L
        arrayList.add(category)
        return arrayList
    }

    fun getFirstTicketType(context: Context): ArrayList<TicketType> {
        val arrayList = ArrayList<TicketType>()
        val tt = TicketType()
        tt.name = context.getString(R.string.select_ticket_type)
        tt.issueTypeId = -1
        arrayList.add(tt)
        return arrayList
    }

    fun setProductListing(productsList: ArrayList<ProductDetail>) {
        products = productsList
    }

    fun getProductsByCategory(string: String?): ArrayList<ProductDetail> {

        if (string == "All Products") {
            return if (::products.isInitialized) {
                products
            } else {
                arrayListOf()
            }
        }

        return if (::products.isInitialized) {
            val list = ArrayList<ProductDetail>()
            for (product in products) if (product.group == string) list.add(product)
            list
        } else {
            arrayListOf()
        }
    }

    fun setProductCatList(list: ArrayList<ProductCategory>) {
        productCategoryList = list
    }

    fun setPackCatList(list: ArrayList<PackCategory>) {
        packCategoryList = list
    }

    fun getPackCatList(): ArrayList<PackCategory> {
        return if (::packCategoryList.isInitialized) packCategoryList else arrayListOf()
    }

    fun getProductCatList(): ArrayList<ProductCategory> {
        return if (::productCategoryList.isInitialized) productCategoryList else arrayListOf()
    }

    private var states: ArrayList<StateMaster> = arrayListOf()
    private var districts: ArrayList<DistrictMaster> = arrayListOf()
    private var cities: ArrayList<CityMaster> = arrayListOf()

    private var statePosition: Int = 0
    private var districtPosition: Int = 0
    private var cityPosition: Int = 0

    fun getStates(): ArrayList<StateMaster> = if (states.isNotEmpty()) states else arrayListOf()
    fun getDistricts(): ArrayList<DistrictMaster> =
        if (districts.isNotEmpty()) districts else arrayListOf()

    fun getCities(): ArrayList<CityMaster> = if (cities.isNotEmpty()) cities else arrayListOf()
    fun getStatePosition(): Int = statePosition
    fun getDistrictPosition(): Int = districtPosition
    fun getCityPosition(): Int = cityPosition

    fun setStates(list: ArrayList<StateMaster>) {
        states = list
    }

    fun setDistricts(list: ArrayList<DistrictMaster>) {
        districts = list
    }

    fun setCities(list: ArrayList<CityMaster>) {
        cities = list
    }

    fun setStatePosition(int: Int) {
        statePosition = int
    }

    fun setDistrictPosition(int: Int) {
        districtPosition = int
    }

    fun setCityPosition(int: Int) {
        cityPosition = int
    }


    fun getSegmentString(segments: List<Segments>): String {
        var string = ""
        for (segment in segments) if (segment.isChecked) string =
            (if (string.isEmpty()) segment.segment!! else string + "," + segment.segment)
        return string
    }

    private var segments: ArrayList<Segments> = arrayListOf()
    private var customerSegments: ArrayList<Segments> = arrayListOf()

    fun setSegments(list: ArrayList<Segments>) {
        segments = list
    }

    fun getSegments(): ArrayList<Segments> = segments

    fun getFirstCoupon(context: Context): ArrayList<RedemptionCouponMaster> {
        val arrayList = ArrayList<RedemptionCouponMaster>()
        val coupon = RedemptionCouponMaster()
        coupon.name = context.getString(R.string.select)
        arrayList.add(coupon)
        return arrayList
    }

    fun setFilteredUsers(list: List<User>) {
        filteredUserList = arrayListOf()
        filteredUserList = list
    }

    fun getFilteredUserList(): List<User> {
        return if (::filteredUserList.isInitialized) {
            filteredUserList
        } else {
            arrayListOf()
        }
    }

    fun setCustomerSegments(arrayList: java.util.ArrayList<Segments>) {
        customerSegments = arrayList
    }

    fun getCustomerSegments(): ArrayList<Segments> = customerSegments
    fun setVehicleSegment(vehicleSegment: List<String>) {
        vehicleSegmentList = vehicleSegment
    }

    fun getVehicleSegment() = vehicleSegmentList

    fun getProducts(): ArrayList<ProductDetail> {
        return Paper.book().read<ArrayList<ProductDetail>>("products")
    }

    fun setProducts(list: ArrayList<ProductDetail>) {
        Paper.book().write("products", list)
    }


    fun getIsNewStart(): Boolean {
        return Paper.book().read<Boolean>("isNewStart") ?: true
    }

    fun setNewStartFalse() {
        Paper.book().write("isNewStart", false)
    }

    fun getSortList(context: Context): ArrayList<ProductSort> {
        val sortList = arrayListOf<ProductSort>()
        val ps11 = ProductSort()
        ps11.sortId = 1
        ps11.strSort = context.getString(R.string.atoz)
        sortList.add(ps11)
        val ps22 = ProductSort()
        ps22.sortId = 2
        ps22.strSort = context.getString(R.string.ztoa)
        sortList.add(ps22)
        val ps = ProductSort()
        ps.sortId = 3
        ps.strSort = context.getString(R.string.points_low_to_high)
        sortList.add(ps)
        val ps2 = ProductSort()
        ps2.sortId = 4
        ps2.strSort = context.getString(R.string.points_high_to_low)
        sortList.add(ps2)
        return sortList

    }

    fun setAddCartObject(mProductDetail: ProductDetail) {
        pd = mProductDetail
    }

    fun getAddCartObject(): ProductDetail {
        return pd
    }

    fun showWelcomeBanner(welcomePointsErrorCode: Int?) {
        Paper.book().write("welcomeBanner", welcomePointsErrorCode)
    }

    fun isShowWelcomeBanner(): Boolean {
        val read = Paper.book().read<Int>("welcomeBanner", 0)
        if (read == 1) {
            return true
        }
        return false
    }

    fun getSelectedLanguage(): String {
        return Paper.book().read<String>("selectedLanguage", "en")
    }

    fun setSelectedLanguage(lang: String) {
        Paper.book().write("selectedLanguage", lang)
    }

    fun setDbAppVersion(androidVersion: Int) {
        Paper.book().write(Constants.APP_VERSION, androidVersion)
    }

    fun getDbAppVersion(): Int? {
        return Paper.book().read<Int?>(Constants.APP_VERSION, null)
    }

    fun getSendSelectedLang(): Boolean {
        return Paper.book().read<Boolean>("sendSelectedLanguage", false)
    }

    fun setSendSelectedLang(b: Boolean) {
        Paper.book().write("sendSelectedLanguage", b)
    }

    fun setRetSelectedProdForScan(productDetail: ProductDetail) {
        this.retSelectedProdForScan = productDetail
    }

    fun getRetSelectedProdForScan(): ProductDetail {
        return this.retSelectedProdForScan
    }


    fun getFirstAccessmentYear(context: Context): ArrayList<String> {
        val arrayList = ArrayList<String>()
       // arrayList.add(context.getString(R.string.select_ay))
        return arrayList
    }

}

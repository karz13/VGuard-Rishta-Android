package com.tfl.vguardrishta.utils

object Constants {

    const  val MONTHS: String="Months"
    val WINEER_DATES: String = "Winner_dates"
    val APP_VERSION: String = "app_version"
    val OTP_TYPE_VOICE: String = "Voice"
    val PROFESSION: String = "PROFESSION"
    const val BANKS: String = "BANKS"
    const val LOGIN_OTP_USER_NAME: String = "login_otp_user_name"
    const val IS_LOGIN_WITH_OTP: String = "login_with_otp"
    const val LOGIN_TYPE_PASS = "password"
    const val LOGIN_TYPE_OTP = "otp"

    const val RET_USER_TYPE = "2"
    const val INF_USER_TYPE = "1"
    const val EC_SUB_USER_TYPE = "electrical"
    const val AC_SUB_USER_TYPE = "ac_sub_user"
    const val RET_SUB_USER_TYPE = "ret_sub_user"
    const val AIR_COOLER_ENABLED = "1"
    const val AIR_COOLER_DISABLED = "0"
    const val CONTACT_US: String = "contact_us"
    const val ID_TYPES: String = "id_types"
    const val NOTIFICATION: String = "NOTIFICATION"
    const val KEY_USER_REFRESH_REQ: String = "user_refreshreq"
    const val SORT_POINTS_LOW_TO_HIGH: String = "Points low to high"
    const val SORT_POINTS_HIGH_TO_LOW: String = "Points high to low"
    const val CREDS: String = "creds"
    const val KEY_USER: String = "key_user"
    const val KEY_USER_CREDS: String = "key_user_creds"
    const val KEY_RISHTA_USER: String = "key_rishta_user"

    const val HOME_FRAGMENT: String = "HomeFragment"
    const val USER_VERIFICATION: String = "UserVerification"
    const val DASHBOARD_FRAGMENT: String = "DashboardFragment"
    const val LOGIN_WITH_OTP_FRAGMENT: String = "LoginWithOtpFragment"
    const val FORGOT_PASSWORD_FRAGMENT: String = "ForgotPasswordFragment"
    const val PRODUCT_CATEGORY_FRAGMENT: String = "ProductCategoryFragment"

    const val RETAILER: String = "DIS"
    const val MECHANIC: String = "RET"
    const val CUSTOMER: String = "CUST"
    const val UNO_MINDA_CARE: String = "ASC"
    const val INDEPENDENT_WORKSHOP = "IWS"

    const val HEAD_MANAGER: String = "HO"
    const val REGIONAL_MANAGER: String = "ZO"
    const val BRANCH_MANAGER: String = "RO"

    const val TERRITORY_MANAGER: String = "APM"

    const val DISTRIBUTOR: String = "DIST"
    const val GROWTH_OFFICER: String = "GO"


    const val CITY: String = "city"
    const val USERS: String = "users"
    const val STATES: String = "state"
    const val CATEGORY: String = "category"
    const val DISTRICT: String = "districtName"
    const val TICKET_TYPE: String = "ticketType"


    const val SORT: String = "sort"
    const val USER_OBJECT: String = "userObject"
    const val PRODUCT_CATEGORY: String = "productCategory"
    const val PRODUCT_CATEGORY_NAME: String = "productCategoryName"

    const val PACK_CATEGORY_ID: String = "packCategoryId"
    const val PACK_CATEGORY_NAME: String = "packCategoryName"
    const val SELECTED_SEGMENT: String = "selected_segment"

    const val PRODUCT: String = "product"
    const val USER_TYPE: String = "UserType"
    const val USER_ROLE: String = "USER_ROLE"

    const val ORDER = "ORDER"

    const val CHEQUE = "CHEQUE"
    const val ID_CARD_FRONT = "ID_CARD_FRONT"
    const val TICKET = "TICKET"
    const val ID_CARD_BACK = "ID_CARD_BACK"
    const val PAN_CARD_FRONT = "PAN_CARD_FRONT"
    const val PAN_CARD_BACK = "PAN_CARD_BACK"
    const val ERROR_COUPON = "ERROR_COUPON"
    const val WARRANTY = "WARRANTY"
    const val BILL: String = "BILL"
    const val GST: String = "GST"

    const val PROFILE = "PROFILE"
    const val WORKSHOP = "WORKSHOP"
    const val INVOICE = "INVOICE"
    const val DISPLAY_CONTEST = "DISPLAY_CONTEST"

    const val CASH: String = "CASH"
    const val PAYTM: String = "PAYTM"
    const val GIFT: String = "Bank Card"
    const val COUPON: String = "Gift Vouchers"
    const val BANK_GIFT_CARD = "Bank Gift Card"
    const val IMAGE_RELATED: String = "image_related"

    const val RESULT_OK = 1
    const val PDF = "terms.pdf"
    const val USER_LIST = "userList"
    const val UPLOAD_ORDER = "UPLOAD_ORDER"

    const val INVOICE_DETAILS = "INVOICE_DETAILS"

    const val IS_RETAILER_COUPON = "is_retailer_coupon"

    const val RETAILER_FULL_NAME: String = "Retailer"
    const val VEHICLE_SEGMENT = "vehicle_segment"

    //Redemption details SP constants for specific screen
    const val HOME_SCREEN = "HOME"
    const val REDEMPTION_SCREEN = "REDEMPTION"

    //air cooler scheme offer
    const val AIR_COOLER_OFFER = "6"
    const val AIRCOOLER_SCREEN = "1"
    interface RetProdCategory {
        companion object {
            const val one_INVERTER_BATTERIES: String = "1";
            const val two_WATER_HEATER = "2"
            const val three_FAN = "3"
            const val four_STABILIZER = "4"
            const val five_AIR_COOLER = "5"

        }
    }
}
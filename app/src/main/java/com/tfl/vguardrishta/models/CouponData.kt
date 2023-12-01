package com.tfl.vguardrishta.models

/**
 * Created by Shanmuka on 1/17/2019.
 */
class CouponData {

    var userMobileNumber: String? = null
    var couponCode: String? = null
    var pin: String? = null
    var smsText: String? = null
    var from: String? = null
    var userType: String? = null
    var userId: Long = 0
    var apmID: Long = 0
    var retailerCoupon: Boolean = false
    var userCode:String?=null
    var isAirCooler: Int? = 0
    var latitude: String? = null
    var longitude: String? = null
    var geolocation: String? = null
    var category: String=""
}
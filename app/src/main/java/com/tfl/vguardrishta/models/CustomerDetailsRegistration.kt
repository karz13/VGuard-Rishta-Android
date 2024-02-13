package com.tfl.vguardrishta.models

class CustomerDetailsRegistration {

    var contactNo: String? = null
    var otp:String? = null
    var name: String? = null
    var email: String? = null
    var currAdd: String? = null
    var alternateNo: String? = null
    var state: String? = null
    var district: String? = null
    var city: String? = null
    var pinCode: String? = null
    var landmark: String? = null
    var dealerName: String? = null
    var dealerAdd: String? = null
    var dealerState: String? = null
    var dealerPinCode: String? = null
    var dealerDist: String? = null
    var dealerCity: String? = null
    var dealerNumber: String? = null
    var dealerCategory: String? = null
    var addedBy: Long? = null

    var transactId: String? = null
    var billDetails: String? = null
    var warrantyPhoto: String? = null
    var sellingPrice: String? = null
    var emptStr = ""

    var selectedProd: ProductDetail = ProductDetail()
    var cresp: CouponResponse = CouponResponse()

    var latitude: String? = null
    var longitude: String? = null
    var geolocation: String? = null

}
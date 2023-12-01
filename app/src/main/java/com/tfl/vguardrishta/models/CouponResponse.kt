package com.tfl.vguardrishta.models

class CouponResponse {

    var errorCode: Int? = -1
    var errorMsg: String? = null
    var statusType: Int = -1
    var balance: String? = null
    var currentPoints: String? = null
    var couponPoints: String? = null
    var promotionPoints: String? = null
    var scanDate: String? = null
    var scanStatus: String? = null
    var copuonCode: String? = ""
    var transactId: String? = null
    var schemePoints: String? = "0"
    var basePoints: String? = "0"
    var bitEligibleScratchCard: Boolean = false

    var clubPoints: String? = "0"

    var pardId: String? = null
    var partNumber: String? = null
    var partName: String? = null
    var couponCode: String? = null
    var skuDetail: String? = null
    var purchaseDate: String? = null
    var categoryId: String?=null
    var category: String?=null

    var warranty: String? = ""

    var anomaly:Int=-1

}

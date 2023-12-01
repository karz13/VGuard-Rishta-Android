package com.tfl.vguardrishta.models

/**
 * Created by Joshi on 16-12-2019.
 */
class DisplayContest {
    var code: Int? = null
    var id: Long? = null // user id  like apm or retailer
    var mobileNo: String? = null
    var image1: String? = null
    var image2: String? = null
    var addedById: String? = null
    var retailerName: String? = null;
    var validityDate: String? = null
    var contestStatus: Int? = null // 0 for open , 1 for closed
    var errorMsg: String? = null
}
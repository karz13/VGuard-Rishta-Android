package com.tfl.vguardrishta.models

import java.io.Serializable

/**
 * Created by Joshi on 24-09-2019.
 */
class InvoiceDetails : Serializable{
    var intInvoiceId: Long? = null
    var strUserName: String? = null
    var strUserId: String? = null
    var strCompanyName: String? = null
    var address: String? = null
    var strStateNAme: String? = null
    var strDistrictName: String? = null
    var territory: String? = null
    var strInvoiceNo: String? = null
    var dtInvoiceDate: String? = null
    var numinvoiceAmount: String? = null
    var strInvoicePhoto: String? = null
    var strDistributorName: String? = null
}
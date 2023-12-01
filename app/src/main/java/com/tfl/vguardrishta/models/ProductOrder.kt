package com.tfl.vguardrishta.models

class ProductOrder {
    var mobileNo: String? = null
    var userId: Long? = null
    var amount: String? = null
    var points: String? = null

    var bankDetail: BankDetail? = null
    var productDetail: ProductDetail? = null
    var shippingAddress: ShippingAddress? = null
    var roleId: String? = null
}

package com.tfl.vguardrishta.models

import java.io.Serializable

class ProductDetail : Serializable {
    var userId: Long? = null
    var item: String? = null
    var group: String? = null
    var productDescription: String? = null
    var productType: String? = null
    var subGroup: String? = null
    var segment: String? = null
    var oem: String? = null
    var application: String? = null
    var mrp: String? = null
    var partGroup: String? = null
    var proImg: String? = null

    var productId: String? = null
    var productName: String? = null
    var productCategory: String? = null
    var productCode: String? = null
    var points: Double = 0.0
    var imageUrl: String? = null
    var isSelected: Boolean = false
    var type: String? = null
}
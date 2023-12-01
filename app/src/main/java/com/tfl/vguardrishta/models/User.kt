package com.tfl.vguardrishta.models

import java.io.Serializable

/**
 * Created by Shanmuka on 19-11-2018.
 */
class User : Serializable {
    var id: Long? = null
    var username: String? = null
    var mobileNo1: String? = null
    var cardType: String? = null
    var cardNo: String? = null
    var cardPhoto: String? = null
    var districtName: String? = null
    var ifscCode: String? = null
    var accountNo: String? = null
    var acntType: String? = null
    var checkbookPhoto: String? = null
    var cityName: String? = null
    var pincode: String? = null
    var photo: String? = null
    var geoLocation: String? = null
    var totalPoints: Double = 0.toDouble()
    var garageName: String? = null
    var garageSegment: String? = null
    var shopName: String? = null
    var shopSegment: String? = null
    var shopPhoto: String? = null
    var chequeBookPhotoText: String? = null
    private var fileUploader: FileUploader? = null
    var parentId: Long? = null
    var parentType: String? = null
    var userType: String? = null
    var mobileNo2: String? = null
    var password: String? = null
    var deletedFlag: Boolean? = null
    var statusFlag: String? = null
    var createdOn: String? = null
    var createdBy: Long? = null
    var lastLoggedOn: String? = null
    var modifiedBy: Long? = null
    var modifiedOn: String? = null
    var dob: String? = null
    var anniversaryDate: String? = null
    var email: String? = null
    var companyName: String? = null
    var companySegment: String? = null
    var address1: String? = null
    var address2: String? = null
    var stateId: String? = null
    var stateName: String? = null
    var cityId: Long? = null
    var bankName: String? = null
    var accountHolderName: String? = null
    var panNo: String? = null
    var roId: Long? = null
    var roName: String? = null
    var aoName: String? = null
    var apmName: String? = null
    var longitude: Float? = null
    var latitude: Float? = null
    var addedFrom: String = "APP"
    var rejectedBy: Long? = null
    var rejectedOn: String? = null
    var remarks: String? = null
    var approvedBy: Long? = null
    var approvedOn: String? = null
    var otp: Int? = null
    var code: Int? = null
    var errorMesage: String? = null
    var version: Int? = null
    var points: Points? = null
    var otpType: Int? = null //1 for Regitration 2-Login
    var companyPhoto: String? = null
    var pushNotificationToken: String? = null
    var imagesToDelete: String? = null //we have to get in comma separated
    var strAoId: String? = null
    var strApmId: String? = null
    var strDistrictId: String? = null
    var isUpdate: Boolean = false
    var activeFlag: Boolean? = null
    var userTypeFullName: String? = null
    var votpFlag: Int? = null
    var customerSegment: String? = null
    var isAccessories: Boolean? = null
    var monthlyTarget: Int? = null
    var isLogout: Boolean? = null

    var userName: String? = null
    var userCode: String? = null
    var userId: String? = null
    var displayName: String? = null
 }

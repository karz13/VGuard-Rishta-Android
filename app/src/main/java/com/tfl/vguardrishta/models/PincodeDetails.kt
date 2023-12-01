package com.tfl.vguardrishta.models

class PincodeDetails {
    var stateId: Int? = null
    var stateName: String? = null
    var distId: Int? = null
    var distName: String? = null
    var cityId: Int? = null
    var cityName: String? = null
    var pinCodeId: Int? = null
    var pinCode: Int? = null

    override fun toString(): String {
        return pinCode.toString()
    }


}
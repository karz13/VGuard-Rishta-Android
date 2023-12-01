package com.tfl.vguardrishta.models

/**
 * Created by Shanmuka on 1/17/2019.
 */
class Status {
    var code: Int = 0
    var message: String = ""
    var entityUid: String? = null
    var errorCode: Int = -1
    var balancePoints: String? = null
      constructor() {
    }

    constructor(code: Int, message: String) {
        this.code = code
        this.message = message
    }
}
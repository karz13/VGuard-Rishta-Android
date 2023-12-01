package com.tfl.vguardrishta.imageUpload

import com.tfl.vguardrishta.models.User
import com.tfl.vguardrishta.remote.ApiService

/**
 * Created by Shanmuka on 4/16/2019.
 */
object RestAPI {

    fun service(user: User?): ApiService = ServiceGenerator.createService(
        ApiService::class.java,
        ApiService.prodUrl,
        user?.username.toString(),
        user?.password.toString()
    )

}
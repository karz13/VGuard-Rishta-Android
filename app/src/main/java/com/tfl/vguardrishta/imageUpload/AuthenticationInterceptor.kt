package com.tfl.vguardrishta.imageUpload

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by Shanmuka on 2/2/2018.
 */
class AuthenticationInterceptor(private var authToken: String) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder().header("Authorization", authToken).header("Accept", "application/json")
        val request = builder.build()
        return chain.proceed(request)
    }
}
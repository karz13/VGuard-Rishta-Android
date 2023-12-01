package com.tfl.vguardrishta.imageUpload

import android.text.TextUtils
import com.google.gson.GsonBuilder
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Shanmuka on 2/2/2018.
 */
object ServiceGenerator {

    fun <S> createService(serviceClass: Class<S>, baseUrl: String, username: String, password: String): S {
        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val builder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            val authToken = Credentials.basic(username, password)
            val interceptor = AuthenticationInterceptor(authToken)

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor)
            }
            builder.client(httpClient.build())
            val retrofit = builder.build()
            return retrofit.create(serviceClass)
        }
        val retrofit = builder.client(httpClient.build()).build()
        return retrofit.create(serviceClass)
    }
}
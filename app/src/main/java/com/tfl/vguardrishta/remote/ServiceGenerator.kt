package com.tfl.vguardrishta.remote

import android.text.TextUtils
import android.util.Base64
import com.burgstaller.okhttp.digest.Credentials

import com.google.gson.GsonBuilder
import com.tfl.vguardrishta.utils.CacheUtils
import io.paperdb.Paper
import okhttp3.Interceptor

import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by 3FramesLab on 4/7/2017.
 */

object ServiceGenerator {

    fun <S> createService(
        serviceClass: Class<S>,
        baseUrl: String,
        username: String,
        password: String
    ): S {
        val httpClient =
            OkHttpClient.Builder().addInterceptor(headersInterceptor())
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val builder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            val authenticator =
                com.burgstaller.okhttp.digest.DigestAuthenticator(Credentials(username, password))
            httpClient.authenticator(authenticator)

            builder.client(httpClient.build())
            val retrofit = builder.build()
            return retrofit.create(serviceClass)
        }

        val retrofit = builder.client(httpClient.build()).build()
        return retrofit.create(serviceClass)
    }

    private fun headersInterceptor() = Interceptor { chain ->
        val authType = CacheUtils.getUserCreds().authType
        chain.proceed(
            chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("authType", authType)
                .build()
        )
    }
}

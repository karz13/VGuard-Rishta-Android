package com.tfl.vguardrishta.di.modules

import android.app.Application
import android.util.Base64
import com.burgstaller.okhttp.digest.Credentials
import com.burgstaller.okhttp.digest.DigestAuthenticator
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tfl.ahe.di.modules.DigestAuthInterceptor
import com.tfl.vguardrishta.di.scope.ApplicationScope
import com.tfl.vguardrishta.remote.ApiService

import dagger.Module
import dagger.Provides
import io.paperdb.Paper
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


@Module
class NetworkModule(
    private val baseUrl: String,
    private val application: Application
) {

    companion object {
        const val CACHE_SIZE = 10 * 1024 * 1024L // 10 MiB
    }

    @ApplicationScope
    @Provides
    fun provideGson() = GsonBuilder().create()

    @ApplicationScope
    @Provides
    fun provideOkHttpCache() = Cache(application.cacheDir, CACHE_SIZE)

    @ApplicationScope
    @Provides
    fun provideOkHttpClient(cache: Cache): OkHttpClient = with(OkHttpClient.Builder()) {
        val headersInterceptor = DigestAuthInterceptor(cache(cache))
        cache(cache).addInterceptor(headersInterceptor).build()
    }
    
    @ApplicationScope
    @Provides
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @ApplicationScope
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    private var authRequest: String = ""

}
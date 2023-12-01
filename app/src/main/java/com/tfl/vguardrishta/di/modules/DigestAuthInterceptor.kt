package com.tfl.ahe.di.modules

import com.burgstaller.okhttp.digest.Credentials
import com.burgstaller.okhttp.digest.DigestAuthenticator
import io.paperdb.Paper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by Shanmuka on 9/6/2019.
 */
class DigestAuthInterceptor
constructor(private val okHttpBuilder: OkHttpClient.Builder) : Interceptor {
    private var registrationToken: String? = null
    var authenticator :DigestAuthenticator? =null

    fun setRegistrationToken(registrationToken: String?) {
        this.registrationToken = registrationToken
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val requestBuilder = request.newBuilder()


        requestBuilder.addHeader("Accept", "application/json")
        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader("authType", "")


//        val loginUser = Paper.book().read<User>(Constants.KEY_USER)
        authenticator =
            DigestAuthenticator(
                Credentials(
                    "",
                    ""
                )
            )
//        if (loginUser != null){
//            val encryption = AppUtils.getEncryption()
//            authenticator =   DigestAuthenticator(
//                Credentials(
//                    encryption.decrypt(loginUser.userName),
//                    encryption.decrypt(loginUser.password)
//                )
//            )
//        }

        okHttpBuilder.authenticator(authenticator).connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS).build()

        return chain.proceed(requestBuilder.build())
    }
}
package com.tfl.ahe.di.modules

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Created by Shanmuka on 9/17/2019.
 */
class DigestAuthenticator :Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
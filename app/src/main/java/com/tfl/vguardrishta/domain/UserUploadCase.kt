package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.models.User

/**
 * Created by Shanmuka on 4/5/2019.
 */
abstract class UserUploadCase <in P, R> {

    @Throws(RuntimeException::class)
    abstract fun execute(user: User): R

}
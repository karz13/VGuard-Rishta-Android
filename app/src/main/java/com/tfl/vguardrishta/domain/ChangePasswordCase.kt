package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.models.ChangePassword

/**
 * Created by Shanmuka on 6/13/2019.
 */
abstract class ChangePasswordCase<in P, R> {

    @Throws(RuntimeException::class)
    abstract fun execute(changePassword: ChangePassword): R

}
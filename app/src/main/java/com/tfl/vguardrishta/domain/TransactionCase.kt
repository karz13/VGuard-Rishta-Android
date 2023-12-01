package com.tfl.vguardrishta.domain

/**
 * Created by Shanmuka on 1/21/2019.
 */
abstract class TransactionCase<in P, R> {

    @Throws(RuntimeException::class)
    abstract fun execute(id : Long): R

}
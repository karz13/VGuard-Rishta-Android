package com.tfl.vguardrishta.domain

/**
 * Created by Shanmuka on 3/22/2019.
 */
abstract class ListUseCase<in P, R> {

    @Throws(RuntimeException::class)
    abstract fun execute(): R

}
package com.tfl.vguardrishta.domain

/**
 * Created by Shanmuka on 4/22/2019.
 */
abstract class ProductsUseCase<in P, R> {

    @Throws(RuntimeException::class)
    abstract fun execute(string: String): R

}
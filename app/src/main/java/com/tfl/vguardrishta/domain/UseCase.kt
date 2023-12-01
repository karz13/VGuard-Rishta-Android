package com.tfl.vguardrishta.domain

abstract class UseCase<in P, R> {

    @Throws(RuntimeException::class)
    abstract fun execute(parameters: P): R

}
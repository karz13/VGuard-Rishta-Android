package com.tfl.vguardrishta.domain

abstract class SelectSegmentUseCase<in P, R> {

    @Throws(RuntimeException::class)
    abstract fun execute(segment: String, category: Long): R
}

package com.tfl.vguardrishta.domain

import com.tfl.vguardrishta.models.FilterObject

/**
 * Created by Shanmuka on 4/22/2019.
 */
abstract class FilterObjectCase<in P, R> {

    @Throws(RuntimeException::class)
    abstract fun execute(filterObject: FilterObject): R

}
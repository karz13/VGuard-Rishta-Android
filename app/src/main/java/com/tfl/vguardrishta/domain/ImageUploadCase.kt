package com.tfl.vguardrishta.domain

import okhttp3.MultipartBody

/**
 * Created by Shanmuka on 4/5/2019.
 */
abstract class ImageUploadCase <in P, R> {

    @Throws(RuntimeException::class)
    abstract fun execute(string: String , multipartBody: MultipartBody.Part): R

}
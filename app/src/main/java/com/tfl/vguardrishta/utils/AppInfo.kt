package com.tfl.vguardrishta.utils

import android.os.Build
import com.tfl.vguardrishta.BuildConfig

data class AppInfo(
    val version: String = BuildConfig.VERSION_NAME,
//    val flavor: String = BuildConfig.FLAVOR,
    val deviceModel: String = Build.MODEL
)
package com.tfl.vguardrishta.di.modules

import android.os.Build
import androidx.multidex.BuildConfig

data class AppInfo(
    val version: String = BuildConfig.VERSION_NAME,
    val flavor: String = BuildConfig.FLAVOR,
    val deviceModel: String = Build.MODEL
)
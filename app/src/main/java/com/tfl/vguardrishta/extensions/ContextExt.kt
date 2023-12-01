package com.tfl.vguardrishta.extensions

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat

fun Context.color(@ColorRes colorResource: Int): Int {
    return ContextCompat.getColor(this, colorResource)
}

fun Context.inflate(@LayoutRes layoutRes: Int): View? {
    return LayoutInflater.from(this).inflate(layoutRes, null)
}

inline val Context.notificationManager
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

fun Context.isPermissionGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

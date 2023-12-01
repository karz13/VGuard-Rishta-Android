package com.tfl.vguardrishta.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

/**
 * Created by Shanmuka on 8/8/2018.
 */
class PermissionUtils(var activity: Activity) {

    lateinit var context: Context

    companion object Network {
        const val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124
        const val REQUEST_CALL_PERMISSION = 4
        const val REQUEST_CAMERA_PERMISSION = 1

        fun isPermissionGranted(context: Context, permission: String): Boolean {
            val res = context.checkCallingOrSelfPermission(permission)
            return res == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun addPermission(permissionsList: MutableList<String>, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission)
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) return false
        }
        return true
    }

    fun requestForAllMandatoryPermissions() {
        val permissionsNeeded = ArrayList<String>()
        val permissionsList = ArrayList<String>()
        if (!addPermission(permissionsList, Manifest.permission.CAMERA)) permissionsNeeded.add("CAMERA")
        if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE)) permissionsNeeded.add("Call")
        if (!addPermission(permissionsList, Manifest.permission.WAKE_LOCK)) permissionsNeeded.add("WAKE_LOCK")
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)) permissionsNeeded.add("GPS")
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)) permissionsNeeded.add("GPS")
        if (!addPermission(permissionsList, Manifest.permission.WRITE_SETTINGS)) permissionsNeeded.add("WRITE_SETTINGS")
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE)) permissionsNeeded.add("READ_PHONE_STATE")
        if (!addPermission(permissionsList, Manifest.permission.SYSTEM_ALERT_WINDOW)) permissionsNeeded.add("SYSTEM_ALERT_WINDOW")
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE)) permissionsNeeded.add("ACCESS_NETWORK_STATE")
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE)) permissionsNeeded.add("Read External Storage")
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) permissionsNeeded.add("Write External Storage")
        if (!addPermission(permissionsList, Manifest.permission.READ_SMS)) permissionsNeeded.add("Read SMS")
        if (!addPermission(permissionsList, Manifest.permission.RECEIVE_SMS)) permissionsNeeded.add("Write SMS")
        ActivityCompat.requestPermissions(activity, permissionsList.toTypedArray(), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
    }
}
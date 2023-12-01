package com.tfl.vguardrishta.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Shanmuka on 16/1/2019.
 */
class PrefUtil(var context: Context) {

    private var sharedPreferences: SharedPreferences? = null
    private val isLoggedIn = "is_logged_in"

    init {
        if (sharedPreferences == null) sharedPreferences = context.getSharedPreferences("Minda", Context.MODE_PRIVATE)
    }

    fun setIsLoggedIn(isLoggedIn: Boolean) {
        val editor = sharedPreferences!!.edit()
        editor.putBoolean(this.isLoggedIn, isLoggedIn)
        editor.apply()
    }

    fun getIsLoggedIn(): Boolean = sharedPreferences!!.getBoolean(isLoggedIn, false)
}
package com.tfl.vguardrishta.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import com.tfl.vguardrishta.App
import java.util.*

object LocaleHelper {
    fun setLocale(context: Context, language: String?): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLayoutDirection(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            App.AppInstance?.setLanguage(language!!)
        } else {
            configuration.locale = locale
            configuration.setLocale(locale)
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            context.createConfigurationContext(configuration)
        } else {
            context.resources.updateConfiguration(configuration, resources.getDisplayMetrics())
            context
        }
    }

    fun setLocale(context: Context): Context {
        val selectedLanguage = CacheUtils.getSelectedLanguage()
        return setLocale(context, selectedLanguage)
    }

}
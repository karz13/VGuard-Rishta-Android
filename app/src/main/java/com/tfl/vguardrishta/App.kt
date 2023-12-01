package com.tfl.vguardrishta

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import com.tfl.vguardrishta.di.components.ApplicationComponent
import com.tfl.vguardrishta.di.components.DaggerApplicationComponent
import com.tfl.vguardrishta.di.modules.ApplicationModule
import com.tfl.vguardrishta.di.modules.NetworkModule
import com.tfl.vguardrishta.remote.ApiService
import com.tfl.vguardrishta.utils.LocaleHelper
import io.paperdb.Paper
import java.util.*


class App : Application() {
    private var sAnalytics: GoogleAnalytics? = null
    private var sTracker: Tracker? = null
    private var locale: Locale? = null

    val applicationComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .networkModule(NetworkModule(ApiService.prodUrl, this))
            .build()
    }


    override fun onCreate() {
        super.onCreate()
        mContext = this
        mInstance = this
        Paper.init(this)
        sAnalytics = GoogleAnalytics.getInstance(this);
        mRes = resources;
    }

    fun getMContext(): Context {
        return mContext!!
    }

    @Synchronized
    public fun getDefaultTracker(): Tracker? {
        if (sTracker == null) {
            sTracker = sAnalytics?.newTracker(R.xml.global_tracker)
        }
        return sTracker
    }


    companion object {
        private var mRes: Resources? = null
        var mContext: Context? = null
        private var mInstance: App? = null
        val res: Resources?
            get() {
                return mRes
            }

        val context: Context?
            get() {
                return mContext
            }

        val AppInstance: App?
            get() {
                return mInstance
            }

    }

    override fun attachBaseContext(base: Context?) {
        mContext = this
        super.attachBaseContext(LocaleHelper.setLocale(base!!, "en"))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        mContext = this
        super.onConfigurationChanged(newConfig)
        LocaleHelper.setLocale(this)
    }

    fun setLanguage(lang: String) {
        val config = mContext!!.resources.configuration
        if ("" != lang && config.locale.language != lang) {
            locale = Locale(lang)
            Locale.setDefault(locale)
            config.locale = locale
            mContext!!.createConfigurationContext(config)
            mContext!!.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        }
    }

}


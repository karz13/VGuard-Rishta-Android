package com.tfl.vguardrishta.di.modules

import android.app.Application
import android.content.Context
import android.util.DisplayMetrics
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.data.DataRepository
import com.tfl.vguardrishta.di.scope.ApplicationScope
import com.tfl.vguardrishta.remote.RemoteDataSource
import com.tfl.vguardrishta.utils.AppInfo
import com.tfl.vguardrishta.utils.RxBus
import dagger.Module
import dagger.Provides

/**
 * Created by Shanmuka on 19-11-2018.
 */
@Module
class ApplicationModule(private val application: App) {

    @ApplicationScope
    @Provides
    fun providesApplicationContext(): Context = application.applicationContext

    @ApplicationScope
    @Provides
    fun providerRxBus() = RxBus()

    @ApplicationScope
    @Provides
    fun providerDisplayMetrics(): DisplayMetrics = application.resources.displayMetrics

    @ApplicationScope
    @Provides
    fun providerDataRepository(remoteDataSource: RemoteDataSource) = DataRepository(remoteDataSource)

    @ApplicationScope
    @Provides
    fun provideAppInfo() = AppInfo()

}
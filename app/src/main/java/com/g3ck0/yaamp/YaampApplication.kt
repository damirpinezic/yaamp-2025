package com.g3ck0.yaamp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for YAAMP
 * Initializes Hilt dependency injection and Timber logging
 */
@HiltAndroidApp
class YaampApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        Timber.d("YAAMP Application initialized")
    }
}

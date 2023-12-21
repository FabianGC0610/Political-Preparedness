package com.example.android.politicalpreparedness

import android.app.Application
import androidx.viewbinding.BuildConfig
import timber.log.Timber

class Application: Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}

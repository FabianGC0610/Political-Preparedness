package com.example.android.politicalpreparedness

import android.app.Application
import androidx.viewbinding.BuildConfig
import com.example.android.politicalpreparedness.repository.PoliticalRepository
import timber.log.Timber

class Application : Application() {

    val repository: PoliticalRepository
        get() = ServiceLocator.provideRepository(this)

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}

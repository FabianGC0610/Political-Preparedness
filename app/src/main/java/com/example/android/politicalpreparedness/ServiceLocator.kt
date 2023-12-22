package com.example.android.politicalpreparedness

import android.content.Context
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.repository.PoliticalPreparednessRepository
import com.example.android.politicalpreparedness.repository.PoliticalRepository
import com.example.android.politicalpreparedness.source.local.IPoliticalLocalDataSource
import com.example.android.politicalpreparedness.source.local.PoliticalLocalDataSource
import com.example.android.politicalpreparedness.source.remote.IPoliticalRemoteDataSource
import com.example.android.politicalpreparedness.source.remote.PoliticalRemoteDataSource
import org.jetbrains.annotations.VisibleForTesting

object ServiceLocator {
    private var database: ElectionDatabase? = null

    private var civicsApiService: CivicsApiService? = null

    @Volatile
    var repository: PoliticalRepository? = null
        @VisibleForTesting set

    fun provideRepository(context: Context): PoliticalRepository {
        synchronized(this) {
            return repository ?: createRepository(context)
        }
    }

    private fun createRepository(context: Context): PoliticalRepository {
        val newRepo = PoliticalPreparednessRepository(
            createLocalDataSource(context),
            createRemoteDataSource(),
        )
        repository = newRepo
        return newRepo
    }

    private fun createLocalDataSource(context: Context): IPoliticalLocalDataSource {
        val database = database ?: createDatabase(context)
        return PoliticalLocalDataSource(database.electionDao)
    }

    private fun createDatabase(context: Context): ElectionDatabase {
        return ElectionDatabase.getInstance(context)
    }

    private fun createRemoteDataSource(): IPoliticalRemoteDataSource {
        civicsApiService = CivicsApi.retrofitService
        return PoliticalRemoteDataSource(civicsApiService!!)
    }
}

package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.source.local.IPoliticalLocalDataSource
import com.example.android.politicalpreparedness.source.remote.IPoliticalRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PoliticalPreparednessRepository(
    private val localDataSource: IPoliticalLocalDataSource,
    private val remoteDataSource: IPoliticalRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PoliticalRepository {

    /**--------------------------Network----------------------------*/
    override suspend fun getUpcomingElections(): Result<List<Election>> {
        return withContext(ioDispatcher) {
            remoteDataSource.getUpcomingElections()
        }
    }

    override suspend fun getVoterInfo(
        address: String,
        id: Int,
        officialOnly: Boolean,
    ): Result<VoterInfoResponse> {
        return withContext(ioDispatcher) {
            remoteDataSource.getVoterInfo(
                address,
                id,
                officialOnly,
            )
        }
    }

    override suspend fun getRepresentatives(
        address: String,
    ): Result<List<Representative>> {
        return withContext(ioDispatcher) {
            remoteDataSource.getRepresentatives(
                address,
            )
        }
    }

    /**--------------------------Database----------------------------*/
    override suspend fun addElectionToSaved(election: Election) {
        return withContext(ioDispatcher) {
            launch { localDataSource.addElectionToSaved(election) }
        }
    }

    override suspend fun getAllElections(): Result<List<Election>> {
        return withContext(ioDispatcher) {
            localDataSource.getAllElections()
        }
    }

    override suspend fun getElection(id: Int): Result<Election> {
        return withContext(ioDispatcher) {
            localDataSource.getElection(id)
        }
    }

    override suspend fun deleteElection(id: Int) {
        withContext(ioDispatcher) {
            launch { localDataSource.deleteElection(id) }
        }
    }

    override suspend fun deleteAllElections() {
        withContext(ioDispatcher) {
            launch { localDataSource.deleteAllElections() }
        }
    }
}

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

package com.example.android.politicalpreparedness.source.local

import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class PoliticalLocalDataSource(
    private val electionDao: ElectionDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IPoliticalLocalDataSource {
    override suspend fun addElectionToSaved(election: Election) = withContext(ioDispatcher) {
        electionDao.insertElection(election)
    }

    override suspend fun getAllElections(): Result<List<Election>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(electionDao.getAllElections())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error retrieving elections")
        }
    }

    override suspend fun getElection(id: Int): Result<Election> = withContext(ioDispatcher) {
        try {
            val election = electionDao.getElection(id)
            return@withContext if (election != null) {
                Result.Success(election)
            } else {
                Result.Error(ELECTION_NOT_FOUND)
            }
        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "Error retrieving election")
        }
    }

    override suspend fun deleteElection(id: Int) = withContext(ioDispatcher) {
        electionDao.deleteElection(id)
    }

    override suspend fun deleteAllElections() = withContext(ioDispatcher) {
        electionDao.deleteAllElections()
    }

    companion object {
        const val ELECTION_NOT_FOUND = "Election not found!"
    }
}

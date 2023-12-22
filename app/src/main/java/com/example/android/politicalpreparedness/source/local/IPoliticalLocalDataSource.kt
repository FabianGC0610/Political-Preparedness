package com.example.android.politicalpreparedness.source.local

import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.Result

interface IPoliticalLocalDataSource {
    suspend fun addElectionToSaved(election: Election)

    suspend fun getAllElections(): Result<List<Election>>

    suspend fun getElection(id: Int): Result<Election>

    suspend fun deleteElection(id: Int)

    suspend fun deleteAllElections()
}

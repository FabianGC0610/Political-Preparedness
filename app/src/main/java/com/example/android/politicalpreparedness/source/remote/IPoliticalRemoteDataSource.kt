package com.example.android.politicalpreparedness.source.remote

import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.Result
import com.example.android.politicalpreparedness.representative.model.Representative

interface IPoliticalRemoteDataSource {
    suspend fun getUpcomingElections(): Result<List<Election>>

    suspend fun getVoterInfo(
        address: String,
        id: Int,
        officialOnly: Boolean = false,
    ): Result<Election>

    suspend fun getRepresentatives(
        address: String,
        includeOffices: Boolean = true,
        levels: List<String>,
        roles: List<String>,
    ): Result<List<Representative>>
}
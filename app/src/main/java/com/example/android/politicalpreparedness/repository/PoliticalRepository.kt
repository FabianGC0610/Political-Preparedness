package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.representative.model.Representative

interface PoliticalRepository {
    /**--------------------------Network----------------------------*/
    suspend fun getUpcomingElections(): Result<List<Election>>

    suspend fun getVoterInfo(
        address: String,
        id: Int,
        officialOnly: Boolean = false,
    ): Result<VoterInfoResponse>

    suspend fun getRepresentatives(
        address: String,
        includeOffices: Boolean = true,
        levels: List<String>,
        roles: List<String>,
    ): Result<List<Representative>>

    /**--------------------------Database----------------------------*/
    suspend fun addElectionToSaved(election: Election)

    suspend fun getAllElections(): Result<List<Election>>

    suspend fun getElection(id: Int): Result<Election>

    suspend fun deleteElection(id: Int)

    suspend fun deleteAllElections()
}

package com.example.android.politicalpreparedness.source.remote

import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.Result
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.Date

class PoliticalRemoteDataSource(
    private val civicsApiService: CivicsApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IPoliticalRemoteDataSource {

    override suspend fun getUpcomingElections(): Result<List<Election>> =
        withContext(ioDispatcher) {
            return@withContext try {
                val response = civicsApiService.getElections()
                if (response.isSuccessful) {
                    val elections = response.body()?.elections ?: emptyList()
                    Result.Success(elections)
                } else {
                    Result.Error("API response error: ${response.code()}")
                }
            } catch (e: Exception) {
                Result.Error("Error while making API call: ${e.message}")
            }
        }

    override suspend fun getVoterInfo(
        address: String,
        id: Int,
        officialOnly: Boolean,
    ): Result<Election> = withContext(ioDispatcher) {
        return@withContext try {
            val response = civicsApiService.getVoterInfo(
                address,
                id.toLong(),
                officialOnly,
            )
            if (response.isSuccessful) {
                val election =
                    response.body()?.election ?: Election(1, "", Date(), Division("", "", ""))
                Result.Success(election)
            } else {
                Result.Error("API response error: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Error while making API call: ${e.message}")
        }
    }

    override suspend fun getRepresentatives(
        address: String,
        includeOffices: Boolean,
        levels: List<String>,
        roles: List<String>,
    ): Result<List<Representative>> = withContext(ioDispatcher) {
        return@withContext try {
            val response = civicsApiService.getRepresentatives(
                address,
                includeOffices,
                levels,
                roles,
            )
            if (response.isSuccessful) {
                val representativeResponse = response.body()
                val offices = representativeResponse?.offices ?: emptyList()
                val officials = representativeResponse?.officials ?: emptyList()

                val representatives = offices.flatMap { office ->
                    office.getRepresentatives(officials)
                }
                Result.Success(representatives)
            } else {
                Result.Error("API response error: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Error while making API call: ${e.message}")
        }
    }
}

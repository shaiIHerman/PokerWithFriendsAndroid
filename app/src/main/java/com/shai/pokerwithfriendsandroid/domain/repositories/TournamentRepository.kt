package com.shai.pokerwithfriendsandroid.domain.repositories

import com.google.firebase.firestore.FieldValue
import com.shai.pokerwithfriendsandroid.data.local.db.daos.SyncInfoDao
import com.shai.pokerwithfriendsandroid.data.local.db.entities.SyncInfoEntity
import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.sources.LocalTournamentDataSource
import com.shai.pokerwithfriendsandroid.data.sources.RemoteTournamentDataSource
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.utils.ApiOperation
import com.shai.pokerwithfriendsandroid.utils.safeApiCall
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentData
import javax.inject.Inject

class TournamentRepository @Inject constructor(
    private val remoteTournamentDataSource: RemoteTournamentDataSource,
    private val localTournamentDataSource: LocalTournamentDataSource,
    private val fireStoreClient: FireStoreClient,
    private val syncInfoDao: SyncInfoDao
) {

    // Function to fetch tournaments from both Room and Firebase
    suspend fun getTournamentsForUser(): ApiOperation<List<LocalTournament>> {

        // Get the timestamp of the last successful sync
        val lastSyncTimestampResult = safeApiCall { syncInfoDao.getLastSyncTimestamp() }
        if (lastSyncTimestampResult is ApiOperation.Failure) {
            return ApiOperation.Failure(lastSyncTimestampResult.exception)
        }
        val lastSyncTimestamp = (lastSyncTimestampResult as ApiOperation.Success).data

        // Fetch new or updated tournaments from Firestore
        val remoteTournamentsResult = remoteTournamentDataSource.fetchTournamentsForUser(lastSyncTimestamp)
        if (remoteTournamentsResult is ApiOperation.Failure) {
            return remoteTournamentsResult
        }
        val remoteTournaments = (remoteTournamentsResult as ApiOperation.Success).data

        // Update the sync timestamp to the most recent time from the remote tournaments
        if (remoteTournaments.isNotEmpty()) {
            val latestSyncTime =
                remoteTournaments.maxOfOrNull { it.dateUpdated } ?: System.currentTimeMillis()
            syncInfoDao.insertSyncInfo(SyncInfoEntity(lastSyncTimestamp = latestSyncTime))
        }

        // Insert new tournaments or update existing into local DB (Room)
        val insertResult = localTournamentDataSource.insertTournament(remoteTournaments)
        if (insertResult is ApiOperation.Failure) {
            return ApiOperation.Failure(insertResult.exception)
        }

        // Fetch tournaments from local Room DB
        return localTournamentDataSource.getTournaments()
    }

    suspend fun addTournament(tournamentData: TournamentData): ApiOperation<String> {
        val players = tournamentData.players?.map { it.documentReference }
        val updatedPlayers = players?.plus(tournamentData.admin)
        val remoteTournamentData = hashMapOf(
            "name" to tournamentData.name,
            "buyIn" to tournamentData.buyIn,
            "players" to updatedPlayers,
            "admin" to tournamentData.admin,
            "dateCreated" to FieldValue.serverTimestamp(),
            "dateUpdated" to FieldValue.serverTimestamp()
        )
        return remoteTournamentDataSource.addTournament(tournament = remoteTournamentData)
    }

    suspend fun getTournamentById(tournamentId: String): ApiOperation<LocalTournament> {
        return localTournamentDataSource.getTournamentById(tournamentId)
    }

    suspend fun addGameToTournament(
        game: String, tournamentId: String
    ): ApiOperation<Void?> {
        return safeApiCall {
            fireStoreClient.updateTournament(game, tournamentId)
        }
    }
}

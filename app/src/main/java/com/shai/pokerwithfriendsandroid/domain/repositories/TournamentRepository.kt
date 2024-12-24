package com.shai.pokerwithfriendsandroid.domain.repositories

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.shai.pokerwithfriendsandroid.data.local.db.daos.SyncInfoDao
import com.shai.pokerwithfriendsandroid.data.local.db.daos.TournamentDao
import com.shai.pokerwithfriendsandroid.data.local.db.entities.SyncInfoEntity
import com.shai.pokerwithfriendsandroid.data.local.db.entities.TournamentEntity
import com.shai.pokerwithfriendsandroid.data.remote.ApiOperation
import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.remote.safeApiCall
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentData
import javax.inject.Inject

class TournamentRepository @Inject constructor(
    private val tournamentDao: TournamentDao,
    private val fireStoreClient: FireStoreClient,
    private val syncInfoDao: SyncInfoDao
) {

    // Function to fetch tournaments from both Room and Firebase
    suspend fun getTournaments(): List<TournamentEntity> {

        // Get the timestamp of the last successful sync
        val lastSyncTimestamp = syncInfoDao.getLastSyncTimestamp()

        // Fetch new or updated tournaments from Firestore
        val remoteTournaments = fireStoreClient.fetchTournaments(lastSyncTimestamp)

        // Update the sync timestamp to the most recent time from the remote tournaments
        if (remoteTournaments.isNotEmpty()) {
            val latestSyncTime =
                remoteTournaments.maxOfOrNull { it.dateCreated } ?: System.currentTimeMillis()
            syncInfoDao.insertSyncInfo(SyncInfoEntity(lastSyncTimestamp = latestSyncTime))
        }

        // Insert new tournaments or update existing into local DB (Room)
        tournamentDao.insertTournament(remoteTournaments)

        // Fetch tournaments from local Room DB
        val localTournaments = tournamentDao.getTournaments()

        return localTournaments
    }

    suspend fun addTournament(tournamentData: TournamentData): ApiOperation<DocumentReference?> {
        val players = tournamentData.players?.map { it.documentReference }
        val updatedPlayers = players?.plus(tournamentData.admin)
        val tournament = hashMapOf(
            "name" to tournamentData.name,
            "buyIn" to tournamentData.buyIn,
            "players" to updatedPlayers,
            "admin" to tournamentData.admin,
            "dateCreated" to FieldValue.serverTimestamp(),
            "dateUpdated" to FieldValue.serverTimestamp()
        )
        return safeApiCall {
            fireStoreClient.createDocument(
                collectionName = "tournaments", data = tournament
            )
        }
    }

    suspend fun getTournamentById(tournamentId: String): ApiOperation<TournamentEntity> {
        return safeApiCall {
            tournamentDao.getTournamentById(tournamentId)
        }
    }

    suspend fun addGameToTournament(game: DocumentReference?, tournamentId: String): ApiOperation<Void?> {
        return safeApiCall {
            fireStoreClient.updateTournament(game, tournamentId)
        }
    }
}

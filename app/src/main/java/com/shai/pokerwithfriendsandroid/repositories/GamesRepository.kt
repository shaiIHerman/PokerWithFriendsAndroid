package com.shai.pokerwithfriendsandroid.repositories

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.shai.pokerwithfriendsandroid.db.local.daos.GamesDao
import com.shai.pokerwithfriendsandroid.db.local.daos.SyncInfoDao
import com.shai.pokerwithfriendsandroid.db.local.daos.TournamentDao
import com.shai.pokerwithfriendsandroid.db.local.models.SyncInfo
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament
import com.shai.pokerwithfriendsandroid.db.remote.ApiOperation
import com.shai.pokerwithfriendsandroid.db.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.db.remote.models.RemoteGame
import com.shai.pokerwithfriendsandroid.db.remote.safeApiCall
import javax.inject.Inject

class GamesRepository @Inject constructor(
    private val gamesDao: GamesDao,
    private val tournamentDao: TournamentDao,
    private val fireStoreClient: FireStoreClient,
    private val syncInfoDao: SyncInfoDao
) {

    // Function to fetch tournaments from both Room and Firebase
    suspend fun getTournaments(): List<Tournament> {
        // Fetch tournaments from local Room DB
        val localTournaments = tournamentDao.getTournaments()

        // Get the timestamp of the last successful sync
        val lastSyncTimestamp = syncInfoDao.getLastSyncTimestamp()

        // Fetch new or updated tournaments from Firestore
        val remoteTournaments = fireStoreClient.fetchTournaments(lastSyncTimestamp)

        // Merge local and remote tournaments
        val allTournaments = localTournaments + remoteTournaments

        // Update the sync timestamp to the most recent time from the remote tournaments
        if (remoteTournaments.isNotEmpty()) {
            val latestSyncTime =
                remoteTournaments.maxOfOrNull { it.dateCreated } ?: System.currentTimeMillis()
            syncInfoDao.insertSyncInfo(SyncInfo(lastSyncTimestamp = latestSyncTime))
        }

        // Insert new tournaments into local DB (Room)
        tournamentDao.insertTournament(remoteTournaments)

        return allTournaments
    }

    suspend fun addGame(tournamentData: Tournament, playerReferences: Set<DocumentReference>): ApiOperation<DocumentReference?> {
        //todo: change the players
//        val players = tournamentData.playerIds.map { Pair(0, fireStoreClient.firestore.collection("users").document(it)) }
        val players = playerReferences.map { Pair(0, it) }
        val game = hashMapOf(
            "active" to true,
            "buyIn" to tournamentData.buyIn,
            "players" to players,
            "dateCreated" to FieldValue.serverTimestamp()
        )
        return safeApiCall {
            fireStoreClient.createDocument(
                collectionName = "games", data = game
            )
        }
    }

    suspend fun getTournamentById(tournamentId: String): ApiOperation<Tournament> {
        return safeApiCall {
            tournamentDao.getTournamentById(tournamentId)
        }
    }

    suspend fun getGamesByIds(gameIds: List<String>): ApiOperation<List<RemoteGame?>> {
        return safeApiCall {
            gameIds.map { gameId -> fireStoreClient.getDocument<RemoteGame>(collectionName = "games", docId = gameId)}
        }
    }
}

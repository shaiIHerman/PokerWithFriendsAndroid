package com.shai.pokerwithfriendsandroid.repositories

import com.google.firebase.firestore.FieldValue
import com.shai.pokerwithfriendsandroid.db.local.daos.SyncInfoDao
import com.shai.pokerwithfriendsandroid.db.local.daos.TournamentDao
import com.shai.pokerwithfriendsandroid.db.local.models.SyncInfo
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament
import com.shai.pokerwithfriendsandroid.db.remote.FireStoreClient
import javax.inject.Inject

class TournamentRepository @Inject constructor(
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

    suspend fun addTournament() {
        val tournament = hashMapOf(
            "name" to "New TournamentXXX",
            "gamesPlayed" to 0,
            "dateCreated" to FieldValue.serverTimestamp()
        )
        fireStoreClient.createDocument(collectionName = "tournaments", data = tournament)
    }
}

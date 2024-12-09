package com.shai.pokerwithfriendsandroid.repositories

import com.shai.pokerwithfriendsandroid.db.local.daos.TournamentDao
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament
import com.shai.pokerwithfriendsandroid.db.remote.FireStoreClient
import javax.inject.Inject

class TournamentRepository @Inject constructor(
    private val tournamentDao: TournamentDao,
    private val fireStoreClient: FireStoreClient
) {

    // Function to fetch tournaments from both Room and Firebase
    suspend fun getTournaments(): List<Tournament> {
        // Get local tournaments first
        val localTournaments = tournamentDao.getTournaments()

        // Check if remote tournaments exist and update local database
        val remoteTournaments = fireStoreClient.fetchTournaments()

        // If remote tournaments are available, update the local database
        if (remoteTournaments.isNotEmpty()) {
            tournamentDao.insertTournaments(remoteTournaments)
        }

        // Return the combined list (local + remote)
        return localTournaments + remoteTournaments
    }

    suspend fun addTournament(tournament: Tournament) {
        tournamentDao.insertTournaments(listOf(tournament))
        fireStoreClient.createDocument(collectionName = "tournaments", data = tournament)
    }
}

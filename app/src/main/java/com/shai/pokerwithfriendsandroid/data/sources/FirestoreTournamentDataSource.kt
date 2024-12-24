package com.shai.pokerwithfriendsandroid.data.sources

import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.remote.models.toLocalTournament
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import javax.inject.Inject

class FirestoreTournamentDataSource @Inject constructor(private val firestoreClient: FireStoreClient) :
    RemoteTournamentDataSource {
    override suspend fun fetchTournaments(lastSyncTimestamp: Long?): List<LocalTournament> {
        val remoteTournaments = firestoreClient.fetchTournaments(lastSyncTimestamp)
        return remoteTournaments.map { remoteTournament ->
            remoteTournament.toLocalTournament()
        }
    }
}

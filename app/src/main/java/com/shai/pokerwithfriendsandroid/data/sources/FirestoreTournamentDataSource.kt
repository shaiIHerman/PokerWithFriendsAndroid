package com.shai.pokerwithfriendsandroid.data.sources

import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.remote.models.toLocalTournament
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.utils.ApiOperation
import com.shai.pokerwithfriendsandroid.utils.safeApiCall
import javax.inject.Inject

class FirestoreTournamentDataSource @Inject constructor(private val firestoreClient: FireStoreClient) :
    RemoteTournamentDataSource {

    override suspend fun fetchTournaments(lastSyncTimestamp: Long?): ApiOperation<List<LocalTournament>> {
        return safeApiCall {
            val remoteTournaments = firestoreClient.fetchTournamentsByLastUpdate(lastSyncTimestamp)
            remoteTournaments.map { remoteTournament ->
                remoteTournament.toLocalTournament()
            }
        }
    }

    override suspend fun addTournament(tournament: HashMap<String, Any?>): ApiOperation<String> {
        return safeApiCall {
            firestoreClient.createDocument(collectionName = "tournaments", data = tournament)
        }
    }
}

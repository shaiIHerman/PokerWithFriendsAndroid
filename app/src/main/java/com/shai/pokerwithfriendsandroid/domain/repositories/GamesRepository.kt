package com.shai.pokerwithfriendsandroid.domain.repositories

import com.google.firebase.firestore.FieldValue
import com.shai.pokerwithfriendsandroid.data.local.db.daos.SyncInfoDao
import com.shai.pokerwithfriendsandroid.data.local.db.daos.TournamentDao
import com.shai.pokerwithfriendsandroid.data.local.db.entities.TournamentEntity
import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.remote.models.RemoteGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.utils.ApiOperation
import com.shai.pokerwithfriendsandroid.utils.safeApiCall
import javax.inject.Inject

class GamesRepository @Inject constructor(
    private val tournamentDao: TournamentDao,
    private val fireStoreClient: FireStoreClient,
    private val syncInfoDao: SyncInfoDao
) {

    suspend fun addGame(
        tournamentData: LocalTournament, playerIds: List<String>
    ): ApiOperation<String> {
        val players = playerIds.map {
            Pair(
                0, fireStoreClient.getDocumentReference(collectionName = "users", docId = it)
            )
        }
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

    suspend fun getTournamentById(tournamentId: String): ApiOperation<TournamentEntity> {
        return safeApiCall {
            tournamentDao.getTournamentById(tournamentId)
        }
    }

    suspend fun getGamesByIds(gameIds: List<String>): ApiOperation<List<RemoteGame?>> {
        return safeApiCall {
            gameIds.map { gameId ->
                fireStoreClient.getDocument<RemoteGame>(
                    collectionName = "games", docId = gameId
                )
            }
        }
    }
}

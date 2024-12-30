package com.shai.pokerwithfriendsandroid.domain.repositories

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.remote.FirestoreRealtimeListener
import com.shai.pokerwithfriendsandroid.data.remote.models.RemoteGame
import com.shai.pokerwithfriendsandroid.data.remote.models.toLocalGame
import com.shai.pokerwithfriendsandroid.data.sources.RemoteGameDataSource
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.utils.ApiOperation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GamesRepository @Inject constructor(
    private val gameDataSource: RemoteGameDataSource,
    private val fireStoreClient: FireStoreClient,
    private val firestoreListener: FirestoreRealtimeListener
) {

    //todo: remove firestore client
    suspend fun addGame(
        tournamentData: LocalTournament, playerIds: List<String>
    ): ApiOperation<String> {
        val players = playerIds.map {
            hashMapOf(
                "position" to 0, "player" to fireStoreClient.getUserDocumentReference(docId = it)
            )

        }
        val game = hashMapOf(
            "active" to true,
            "buyIn" to tournamentData.buyIn,
            "players" to players,
            "dateCreated" to FieldValue.serverTimestamp()
        )
        return gameDataSource.createGame(game = game)
    }

    suspend fun getGamesByIds(gameIds: List<String>): ApiOperation<List<LocalGame>> {
        return gameDataSource.fetchGamesForTournament(gameIds = gameIds)
    }

    suspend fun updatePlayerPositions(game: LocalGame, gameOver: Boolean): ApiOperation<Void?> {
        val players = game.players.map {
            hashMapOf(
                "position" to it.position,
                "player" to fireStoreClient.getUserDocumentReference(docId = it.player!!.id)
            )
        }
        return gameDataSource.updatePlayerPositionsForGame(
            gameId = game.id, players = players, gameOver = gameOver
        )
    }

    fun listenForGameUpdates(gameId: String): Flow<LocalGame> = flow {
        firestoreListener.listenToDocumentChanges<RemoteGame>(
            collection = "games", documentPath = gameId
        ).collect { remoteGame ->
            val players = remoteGame?.players?.map { player ->
                fireStoreClient.getUserByDocReference(player.player!!)
            }
            Log.d("FirestoreListener", "Document snapshot: ${remoteGame?.id}")
            emit(remoteGame?.toLocalGame(players)!!)
        }
    }
}

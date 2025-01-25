package com.shai.pokerwithfriendsandroid.domain.repositories

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.shai.pokerwithfriendsandroid.data.local.db.daos.SyncInfoDao
import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.remote.FirestoreRealtimeListener
import com.shai.pokerwithfriendsandroid.data.remote.models.RemoteGame
import com.shai.pokerwithfriendsandroid.data.remote.models.toLocalGame
import com.shai.pokerwithfriendsandroid.data.sources.LocalGameDataSource
import com.shai.pokerwithfriendsandroid.data.sources.RemoteGameDataSource
import com.shai.pokerwithfriendsandroid.domain.models.GameStatus
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.utils.ApiOperation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GamesRepository @Inject constructor(
    private val remoteGameDataSource: RemoteGameDataSource,
    private val localGameDataSource: LocalGameDataSource,
    private val fireStoreClient: FireStoreClient,
    private val firestoreListener: FirestoreRealtimeListener,
    private val syncInfoDao: SyncInfoDao
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
            "tournamentId" to tournamentData.id,
            "active" to true,
            "buyIn" to tournamentData.buyIn,
            "players" to players,
            "dateCreated" to FieldValue.serverTimestamp(),
            "dateUpdated" to FieldValue.serverTimestamp()
        )
        return remoteGameDataSource.createGame(game = game)
    }

    suspend fun getGamesForTournament(
        tournamentId: String
    ): ApiOperation<List<LocalGame>> {
        val localGamesResult =
            localGameDataSource.getGamesForTournament(tournamentId = tournamentId)
        val knownGameIds = if (localGamesResult is ApiOperation.Success) {
            localGamesResult.data.mapNotNull {
                if (it.status != GameStatus.Active) it.id else null
            }
        } else emptyList()

        Log.d("Shai - games", "known games size = ${knownGameIds.size}")
        val remoteGamesResult = remoteGameDataSource.fetchGamesForTournament(
            tournamentId = tournamentId, knownGameIds = knownGameIds
        )
        if (remoteGamesResult is ApiOperation.Failure) {
            return remoteGamesResult
        }
        val games = (remoteGamesResult as ApiOperation.Success).data
        Log.d("Shai - games", "remote games size = ${games.size}")

        // Insert new tournaments or update existing into local DB (Room)
        val insertResult = localGameDataSource.insertGame(remoteGames = games)
        if (insertResult is ApiOperation.Failure) {
            return ApiOperation.Failure(insertResult.exception)
        }
        return localGameDataSource.getGamesForTournament(tournamentId = tournamentId)
    }

    suspend fun updatePlayerPositions(game: LocalGame, gameOver: Boolean): ApiOperation<Void?> {
        val players = game.players.map {
            hashMapOf(
                "position" to it.position,
                "player" to fireStoreClient.getUserDocumentReference(docId = it.player!!.id)
            )
        }
        return remoteGameDataSource.updatePlayerPositionsForGame(
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

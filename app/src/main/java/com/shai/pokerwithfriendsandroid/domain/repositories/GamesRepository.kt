package com.shai.pokerwithfriendsandroid.domain.repositories

import com.google.firebase.firestore.FieldValue
import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.sources.RemoteGameDataSource
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.utils.ApiOperation
import javax.inject.Inject

class GamesRepository @Inject constructor(
    private val gameDataSource: RemoteGameDataSource, private val fireStoreClient: FireStoreClient
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
            gameId = game.id,
            players = players,
            gameOver = gameOver
        )
    }
}

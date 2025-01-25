package com.shai.pokerwithfriendsandroid.data.sources

import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.remote.models.toLocalGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.utils.ApiOperation
import com.shai.pokerwithfriendsandroid.utils.safeApiCall
import javax.inject.Inject

class FirestoreGameDataSource @Inject constructor(private val firestoreClient: FireStoreClient) :
    RemoteGameDataSource {
    override suspend fun fetchGamesForTournament(
        tournamentId: String,
        knownGameIds: List<String>,
    ): ApiOperation<List<LocalGame>> {
        return safeApiCall {
//            gameIds.mapNotNull { gameId ->
            val remoteGames = firestoreClient.fetchGamesForTournament(
                tournamentId = tournamentId,
                knownGameIds = knownGameIds
            )
            remoteGames.map { remoteGame ->
                val players = remoteGame.players.map { player ->
                    firestoreClient.getUserByDocReference(player.player!!)
                }
                remoteGame.toLocalGame(players)
            }
        }
    }

    override suspend fun createGame(game: HashMap<String, Any>): ApiOperation<String> {
        return safeApiCall { firestoreClient.createGame(data = game) }
    }

//    override suspend fun getGameById(gameId: String): ApiOperation<LocalGame?> {
//        return safeApiCall {
//            val remoteGame = firestoreClient.getGameById(
//                gameId = gameId,
////                lastSyncTimestamp = lastSyncTimestamp
//            )
//            val players = remoteGame?.players?.map { player ->
//                firestoreClient.getUserByDocReference(player.player!!)
//            }
//            remoteGame?.toLocalGame(players)
//        }
//    }

    override suspend fun updatePlayerPositionsForGame(
        gameId: String, players: List<java.util.HashMap<String, Any>>, gameOver: Boolean
    ): ApiOperation<Void?> {
        return safeApiCall {
            firestoreClient.updatePlayerPositionsForGame(
                gameId, players, gameOver
            )
        }
    }
}

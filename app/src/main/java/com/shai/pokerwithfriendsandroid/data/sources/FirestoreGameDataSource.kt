package com.shai.pokerwithfriendsandroid.data.sources

import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.remote.models.toLocalGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.utils.ApiOperation
import com.shai.pokerwithfriendsandroid.utils.safeApiCall
import javax.inject.Inject

class FirestoreGameDataSource @Inject constructor(private val firestoreClient: FireStoreClient) :
    RemoteGameDataSource {
    override suspend fun fetchGamesForTournament(gameIds: List<String>): ApiOperation<List<LocalGame>> {
        return safeApiCall {
            gameIds.mapNotNull { gameId ->
                firestoreClient.getGameById(gameId = gameId)?.toLocalGame()
            }
        }
    }

    override suspend fun createGame(game: HashMap<String, Any>): ApiOperation<String> {
        return safeApiCall { firestoreClient.createGame(data = game) }
    }

    override suspend fun getGameById(gameId: String): ApiOperation<LocalGame?> {
        return safeApiCall { firestoreClient.getGameById(gameId = gameId)?.toLocalGame() }
    }
}

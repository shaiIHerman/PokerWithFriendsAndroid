package com.shai.pokerwithfriendsandroid.data.sources

import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.utils.ApiOperation

interface RemoteGameDataSource {
    suspend fun fetchGamesForTournament(gameIds: List<String>): ApiOperation<List<LocalGame>>
    suspend fun createGame(game: HashMap<String, Any>): ApiOperation<String>
    suspend fun getGameById(gameId: String): ApiOperation<LocalGame?>
    suspend fun updatePlayerPositionsForGame(
        gameId: String,
        players: List<java.util.HashMap<String, Any>>,
        gameOver: Boolean
    ): ApiOperation<Void?>
}
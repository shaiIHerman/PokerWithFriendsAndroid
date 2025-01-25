package com.shai.pokerwithfriendsandroid.data.sources

import com.shai.pokerwithfriendsandroid.data.local.db.daos.GameDao
import com.shai.pokerwithfriendsandroid.data.local.db.entities.toLocalGameList
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.domain.models.toGameEntity
import com.shai.pokerwithfriendsandroid.utils.ApiOperation
import com.shai.pokerwithfriendsandroid.utils.safeApiCall
import javax.inject.Inject

class RoomGameDataSource @Inject constructor(private val gamesDao: GameDao) : LocalGameDataSource {

    override suspend fun insertGame(remoteGames: List<LocalGame>): ApiOperation<Boolean> {
        val result = safeApiCall { gamesDao.insertGame(remoteGames.map { it.toGameEntity() }) }
        return if (result is ApiOperation.Success) {
            ApiOperation.Success(true)
        } else {
            ApiOperation.Failure((result as ApiOperation.Failure).exception)
        }
    }

    override suspend fun getGamesForTournament(tournamentId: String): ApiOperation<List<LocalGame>> {
        return safeApiCall {
            gamesDao.getGamesForTournament(tournamentId = tournamentId).toLocalGameList()
        }
    }
}
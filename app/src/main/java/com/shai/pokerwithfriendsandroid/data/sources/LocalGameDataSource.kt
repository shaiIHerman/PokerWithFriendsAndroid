package com.shai.pokerwithfriendsandroid.data.sources

import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.utils.ApiOperation

interface LocalGameDataSource {
    suspend fun insertGame(remoteGames: List<LocalGame>): ApiOperation<Boolean>
    suspend fun getGamesForTournament(tournamentId: String): ApiOperation<List<LocalGame>>
//    suspend fun getTournamentById(tournamentId: String): ApiOperation<LocalTournament>
}
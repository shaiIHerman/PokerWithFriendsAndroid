package com.shai.pokerwithfriendsandroid.data.sources

import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.utils.ApiOperation

interface LocalTournamentDataSource {
    suspend fun insertTournament(remoteTournaments: List<LocalTournament>): ApiOperation<Boolean>
    suspend fun getTournaments(): ApiOperation<List<LocalTournament>>
    suspend fun getTournamentById(tournamentId: String): ApiOperation<LocalTournament>
}
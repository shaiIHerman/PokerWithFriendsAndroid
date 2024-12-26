package com.shai.pokerwithfriendsandroid.data.sources

import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.utils.ApiOperation

interface RemoteTournamentDataSource {
    suspend fun fetchTournamentsForUser(lastSyncTimestamp: Long?): ApiOperation<List<LocalTournament>>
    suspend fun addTournament(tournament: HashMap<String, Any?>): ApiOperation<String>
}
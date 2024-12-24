package com.shai.pokerwithfriendsandroid.data.sources

import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament

interface RemoteTournamentDataSource {
    suspend fun fetchTournaments(lastSyncTimestamp: Long?): List<LocalTournament>
}
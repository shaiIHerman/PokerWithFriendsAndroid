package com.shai.pokerwithfriendsandroid.data.sources

import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament

interface LocalTournamentDataSource {
    suspend fun insertTournament(remoteTournaments: List<LocalTournament>)
}
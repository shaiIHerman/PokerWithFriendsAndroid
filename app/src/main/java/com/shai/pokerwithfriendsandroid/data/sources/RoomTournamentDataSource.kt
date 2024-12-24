package com.shai.pokerwithfriendsandroid.data.sources

import com.shai.pokerwithfriendsandroid.data.local.db.daos.TournamentDao
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.domain.models.toTournamentEntity
import javax.inject.Inject

class RoomTournamentDataSource @Inject constructor(private val tournamentDao: TournamentDao) :
    LocalTournamentDataSource {

    override suspend fun insertTournament(remoteTournaments: List<LocalTournament>) {
        tournamentDao.insertTournament(remoteTournaments.map { it.toTournamentEntity() })
    }
}

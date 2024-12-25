package com.shai.pokerwithfriendsandroid.data.sources

import com.shai.pokerwithfriendsandroid.data.local.db.daos.TournamentDao
import com.shai.pokerwithfriendsandroid.data.local.db.entities.toLocalTournament
import com.shai.pokerwithfriendsandroid.data.local.db.entities.toLocalTournamentList
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.domain.models.toTournamentEntity
import com.shai.pokerwithfriendsandroid.utils.ApiOperation
import com.shai.pokerwithfriendsandroid.utils.safeApiCall
import javax.inject.Inject

class RoomTournamentDataSource @Inject constructor(private val tournamentDao: TournamentDao) :
    LocalTournamentDataSource {

    override suspend fun insertTournament(remoteTournaments: List<LocalTournament>): ApiOperation<Boolean> {
        val result =
            safeApiCall { tournamentDao.insertTournament(remoteTournaments.map { it.toTournamentEntity() }) }
        return if (result is ApiOperation.Success) {
            ApiOperation.Success(true)
        } else {
            ApiOperation.Failure((result as ApiOperation.Failure).exception)
        }
    }

    override suspend fun getTournaments(): ApiOperation<List<LocalTournament>> {
        return safeApiCall { tournamentDao.getTournaments().toLocalTournamentList() }
    }

    override suspend fun getTournamentById(tournamentId: String): ApiOperation<LocalTournament> {
        return safeApiCall { tournamentDao.getTournamentById(tournamentId).toLocalTournament() }
    }
}
package com.shai.pokerwithfriendsandroid.data.local.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shai.pokerwithfriendsandroid.data.local.db.entities.TournamentEntity

@Dao
interface TournamentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournaments: List<TournamentEntity>)

    @Query("SELECT * FROM tournaments")
    suspend fun getTournaments(): List<TournamentEntity>

    @Query("SELECT * FROM tournaments WHERE id = :tournamentId LIMIT 1")
    suspend fun getTournamentById(tournamentId: String): TournamentEntity
}

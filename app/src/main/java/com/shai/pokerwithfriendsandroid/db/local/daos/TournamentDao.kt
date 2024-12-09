package com.shai.pokerwithfriendsandroid.db.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament

@Dao
interface TournamentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournaments(tournaments: List<Tournament>)

    @Query("SELECT * FROM tournaments")
    suspend fun getTournaments(): List<Tournament>
}

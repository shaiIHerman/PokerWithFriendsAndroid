package com.shai.pokerwithfriendsandroid.data.local.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shai.pokerwithfriendsandroid.data.local.db.entities.GameEntity

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(games: List<GameEntity>)

    @Query("SELECT * FROM games WHERE tournamentId = :tournamentId")
    suspend fun getGamesForTournament(tournamentId: String): List<GameEntity>
}



package com.shai.pokerwithfriendsandroid.db.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shai.pokerwithfriendsandroid.db.local.models.Game
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament

@Dao
interface GamesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(tournaments: List<Game>)

    @Query("SELECT * FROM games WHERE id IN (:gameIds)")
    suspend fun getGamesByIds(gameIds: List<String>): List<Game>
}

package com.shai.pokerwithfriendsandroid.db.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shai.pokerwithfriendsandroid.db.local.daos.GamesDao
import com.shai.pokerwithfriendsandroid.db.local.daos.SyncInfoDao
import com.shai.pokerwithfriendsandroid.db.local.daos.TournamentDao
import com.shai.pokerwithfriendsandroid.db.local.models.Game
import com.shai.pokerwithfriendsandroid.db.local.models.SyncInfo
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament
import com.shai.pokerwithfriendsandroid.db.local.utils.Converters

@Database(
    entities = [Tournament::class, SyncInfo::class, Game::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tournamentDao(): TournamentDao
    abstract fun gamesDao(): GamesDao
    abstract fun SyncInfoDao(): SyncInfoDao
}

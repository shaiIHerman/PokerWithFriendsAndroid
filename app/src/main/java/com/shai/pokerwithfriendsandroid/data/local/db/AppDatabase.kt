package com.shai.pokerwithfriendsandroid.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shai.pokerwithfriendsandroid.data.local.db.daos.SyncInfoDao
import com.shai.pokerwithfriendsandroid.data.local.db.daos.TournamentDao
import com.shai.pokerwithfriendsandroid.data.local.db.entities.SyncInfoEntity
import com.shai.pokerwithfriendsandroid.data.local.db.entities.TournamentEntity
import com.shai.pokerwithfriendsandroid.data.local.db.utils.Converters

@Database(
    entities = [TournamentEntity::class, SyncInfoEntity::class],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tournamentDao(): TournamentDao
    abstract fun SyncInfoDao(): SyncInfoDao
}

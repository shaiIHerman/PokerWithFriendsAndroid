package com.shai.pokerwithfriendsandroid.db.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shai.pokerwithfriendsandroid.db.local.daos.SyncInfoDao
import com.shai.pokerwithfriendsandroid.db.local.daos.TournamentDao
import com.shai.pokerwithfriendsandroid.db.local.models.SyncInfo
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament

@Database(entities = [Tournament::class, SyncInfo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tournamentDao(): TournamentDao
    abstract fun SyncInfoDao(): SyncInfoDao
}

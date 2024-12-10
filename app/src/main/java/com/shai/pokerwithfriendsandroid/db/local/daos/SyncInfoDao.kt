package com.shai.pokerwithfriendsandroid.db.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shai.pokerwithfriendsandroid.db.local.models.SyncInfo

@Dao
interface SyncInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncInfo(syncInfo: SyncInfo)

    @Query("SELECT lastSyncTimestamp FROM sync_info WHERE id = 1")
    suspend fun getLastSyncTimestamp(): Long?
}

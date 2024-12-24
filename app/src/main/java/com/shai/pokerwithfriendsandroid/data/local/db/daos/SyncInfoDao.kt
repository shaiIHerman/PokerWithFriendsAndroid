package com.shai.pokerwithfriendsandroid.data.local.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shai.pokerwithfriendsandroid.data.local.db.entities.SyncInfoEntity

@Dao
interface SyncInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncInfo(syncInfo: SyncInfoEntity)

    @Query("SELECT lastSyncTimestamp FROM sync_info WHERE id = 1")
    suspend fun getLastSyncTimestamp(): Long?
}

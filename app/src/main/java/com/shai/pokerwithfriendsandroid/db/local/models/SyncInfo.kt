package com.shai.pokerwithfriendsandroid.db.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_info")
data class SyncInfo(
    @PrimaryKey val id: Int = 1, // Only one record for sync info
    val lastSyncTimestamp: Long // Store the timestamp of the last successful sync
)

package com.shai.pokerwithfriendsandroid.db.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tournaments")
data class Tournament(
    @PrimaryKey val id: String,
    val name: String,
    val gamesPlayed: Int,
    val dateCreated: Long = 0
)

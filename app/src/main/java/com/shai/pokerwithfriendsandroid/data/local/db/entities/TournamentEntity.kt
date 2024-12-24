package com.shai.pokerwithfriendsandroid.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val buyIn: String,
    val playerIds: List<String>,
    val gameIds: List<String>,
    val dateCreated: Long = 0,
    val adminId: String = ""
)

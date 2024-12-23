package com.shai.pokerwithfriendsandroid.db.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey val id: String,
    val buyIn: String,
    val dateCreated: Long = 0,
//    val players: List<Pair<Int, String>> = emptyList(),
    var active: Boolean = false
)

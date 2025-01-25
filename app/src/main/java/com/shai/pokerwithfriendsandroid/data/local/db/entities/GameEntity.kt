package com.shai.pokerwithfriendsandroid.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.domain.models.toGameStatus

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: String,
    val status: String,
    val buyIn: String,
    val playerPositionsJson: String,
    val tournamentId: String,
    val dateCreated: Long = 0,
    val dateUpdated: Long = 0,
)

fun List<GameEntity>.toLocalGameList(): List<LocalGame> {
    val gson = Gson()
    return this.map {
        LocalGame(
            id = it.id,
            buyIn = it.buyIn,
            status = it.status.toGameStatus(),
            players = gson.fromJson(
                it.playerPositionsJson, object : TypeToken<List<LocalGame.PlayerPosition>>() {}.type
            ),
            tournamentId = it.tournamentId,
            dateCreated = it.dateCreated,
            dateUpdated = it.dateUpdated
        )
    }
}
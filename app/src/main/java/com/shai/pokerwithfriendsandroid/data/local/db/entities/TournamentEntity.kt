package com.shai.pokerwithfriendsandroid.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val buyIn: String,
    val playerIds: List<String>,
    val gameIds: List<String>,
    val dateCreated: Long = 0,
    val dateUpdated: Long = 0,
    val adminId: String = ""
)

fun List<TournamentEntity>.toLocalTournamentList(): List<LocalTournament> {
    return this.map {
        LocalTournament(
            id = it.id,
            name = it.name,
            buyIn = it.buyIn,
            playerIds = it.playerIds,
            gameIds = it.gameIds,
            dateCreated = it.dateCreated,
            dateUpdated = it.dateUpdated,
            adminId = it.adminId
        )
    }
}

fun TournamentEntity.toLocalTournament(): LocalTournament {
    return LocalTournament(
        id = this.id,
        name = this.name,
        buyIn = this.buyIn,
        gameIds = this.gameIds,
        playerIds = this.playerIds,
        dateCreated = this.dateCreated,
        adminId = this.adminId
    )
}

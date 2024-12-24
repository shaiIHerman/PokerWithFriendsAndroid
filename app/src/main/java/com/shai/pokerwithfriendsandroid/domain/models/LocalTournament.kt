package com.shai.pokerwithfriendsandroid.domain.models

import com.shai.pokerwithfriendsandroid.data.local.db.entities.TournamentEntity

data class LocalTournament(
    val id: String,
    val name: String,
    val buyIn: String,
    val playerIds: List<String>,
    val gameIds: List<String>,
    val dateCreated: Long = 0,
    val adminId: String = ""
)

fun LocalTournament.toTournamentEntity(): TournamentEntity {
    return TournamentEntity(
        id = id,
        name = name,
        buyIn = buyIn,
        playerIds = playerIds,
        gameIds = gameIds,
        dateCreated = dateCreated,
        adminId = adminId
    )
}



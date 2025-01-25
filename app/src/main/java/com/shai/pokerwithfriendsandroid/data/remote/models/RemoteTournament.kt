package com.shai.pokerwithfriendsandroid.data.remote.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament

data class RemoteTournament(
    override var id: String = "",
    val name: String,
    val buyIn: String,
    val players: List<DocumentReference>,
    val games: List<DocumentReference>,
    val dateCreated: Timestamp,
    val dateUpdated: Timestamp,
    val admin: DocumentReference?,
) :WithId{
    constructor() : this(
        "", "", "", emptyList(), emptyList(),  Timestamp.now(), Timestamp.now(), null
    )
}

fun RemoteTournament.toLocalTournament(): LocalTournament {
    val dateCreated = dateCreated.toDate().time
    val dateUpdated = dateUpdated.toDate().time
    val playerIds = players.map { it.id }
    val games = games.map { it.id }
    val adminId = admin?.id ?: ""
    return LocalTournament(
        id = id,
        name = name,
        dateCreated = dateCreated,
        dateUpdated = dateUpdated,
        gameIds = games,
        buyIn = buyIn,
        playerIds = playerIds,
        adminId = adminId
    )
}

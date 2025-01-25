package com.shai.pokerwithfriendsandroid.data.remote.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.shai.pokerwithfriendsandroid.domain.models.GameStatus
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame

data class RemoteGame(
    override var id: String = "",
    val active: Boolean,
    val buyIn: String,
    val players: List<PlayerPosition>,
    val tournamentId: String,
    val dateCreated: Timestamp,
    val dateUpdated: Timestamp
) : WithId {
    constructor() : this(
        "", false, "", emptyList(), "", Timestamp.now(), Timestamp.now()
    )

    data class PlayerPosition(val position: Int, val player: DocumentReference?) {
        constructor() : this(0, null)
    }
}

fun RemoteGame.toLocalGame(users: List<RemoteUser?>?): LocalGame {
    val dateCreated = dateCreated.toDate().time
    val dateUpdated = dateUpdated.toDate().time
    val gameStatus = when (active) {
        true -> GameStatus.Active
        false -> GameStatus.Completed
    }
    var localPlayers = emptyList<LocalGame.PlayerPosition>()
    if (users != null) {
        localPlayers = users.map { player ->
            val pl = players.indexOfFirst { it.player!!.id == player!!.id }
            LocalGame.PlayerPosition(players[pl].position, player?.toLocalUser())
        }
    }
    return LocalGame(
        id = id,
        status = gameStatus,
        buyIn = buyIn,
        players = localPlayers,
        tournamentId = tournamentId,
        dateCreated = dateCreated,
        dateUpdated = dateUpdated
    )
}
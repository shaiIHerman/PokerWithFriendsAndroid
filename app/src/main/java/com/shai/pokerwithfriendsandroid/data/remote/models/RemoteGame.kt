package com.shai.pokerwithfriendsandroid.data.remote.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame

data class RemoteGame(
    override var id: String = "",
    val active: Boolean,
    val buyIn: String,
    val players: List<PlayerPosition>,
    val dateCreated: Timestamp
) : WithId {
    constructor() : this(
        "", false, "", emptyList(), Timestamp.now()
    )

    data class PlayerPosition(val position: Int, val player: DocumentReference?) {
        constructor() : this(0, null)
    }
}

fun RemoteGame.toLocalGame(): LocalGame {
    val dateCreated = dateCreated.toDate().time
    return LocalGame(
        id = id,
        active = active,
        buyIn = buyIn,
        players = players,
        dateCreated = dateCreated
    )
}
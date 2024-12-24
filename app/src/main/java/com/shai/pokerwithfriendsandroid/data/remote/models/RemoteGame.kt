package com.shai.pokerwithfriendsandroid.data.remote.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class RemoteGame(
    val active: Boolean,
    val buyIn: String,
    val players: List<PlayerPosition>,
    val dateCreated: Timestamp
) {
    constructor() : this(false, "", emptyList(), Timestamp.now())

    data class PlayerPosition(val position: Int, val player: DocumentReference?){
        constructor() : this(0, null)
    }
}
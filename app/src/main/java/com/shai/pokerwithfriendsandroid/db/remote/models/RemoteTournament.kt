package com.shai.pokerwithfriendsandroid.db.remote.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class RemoteTournament(
    val name: String,
    val buyIn: String,
    val players: List<DocumentReference>,
    val games: List<DocumentReference>,
    val dateCreated: Timestamp,
    val dateUpdated: Timestamp,
    val admin: DocumentReference?,
) {
    constructor() : this("", "", emptyList(), emptyList(), Timestamp.now(), Timestamp.now(), null)
}

package com.shai.pokerwithfriendsandroid.domain.models

data class LocalGame(
    val id: String = "",
    val status: GameStatus,
    val buyIn: String,
    var players: List<PlayerPosition>,
    val dateCreated: Long
) {
    data class PlayerPosition(var position: Int, val player: LocalUser?) {
        constructor() : this(0, null)
    }
}
// todo: player position needs to be converted to a local object as well



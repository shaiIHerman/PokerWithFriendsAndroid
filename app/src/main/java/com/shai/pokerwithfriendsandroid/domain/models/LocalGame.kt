package com.shai.pokerwithfriendsandroid.domain.models

import android.util.Log

data class LocalGame(
    val id: String = "",
    val status: GameStatus,
    val buyIn: String,
    var players: List<PlayerPosition>,
    val dateCreated: Long
) {
    data class PlayerPosition(var position: Int, val player: LocalUser?) {
        constructor() : this(0, null)

        override fun equals(other: Any?): Boolean {
            Log.d("LocalGame PlayerPosition", "position: ${position == (other as PlayerPosition).position}")
            Log.d("LocalGame PlayerPosition", "player: ${player?.id == (other as PlayerPosition).player?.id}")
            return true
//            if (this === other) return true
//            if (other !is PlayerPosition) return false
//            return position == other.position &&
//                    player == other.player // Assuming LocalUser has a proper equals method
        }

        override fun hashCode(): Int {
            return 31 * position + (player?.hashCode() ?: 0)
        }
    }

    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is LocalGame) return false
//
//        return id == other.id &&
//                status == other.status &&
//                buyIn == other.buyIn &&
//                dateCreated == other.dateCreated &&
//                players.size == other.players.size &&
//                players.zip(other.players).all { (thisPlayer, otherPlayer) -> thisPlayer == otherPlayer }
        Log.d("LocalGame", "id: ${id == (other as LocalGame).id}")
        Log.d("LocalGame", "buyIn: ${buyIn == (other as LocalGame).buyIn}")
        Log.d("LocalGame", "dateCreated: ${dateCreated == (other as LocalGame).dateCreated}")
        Log.d("LocalGame", "players size: ${players.size == (other as LocalGame).players.size}")
        Log.d("LocalGame", "players: ${players == (other as LocalGame).players}")
        return false
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + buyIn.hashCode()
        result = 31 * result + dateCreated.hashCode()
        result = 31 * result + players.hashCode()
        return result
    }
}

// todo: player position needs to be converted to a local object as well



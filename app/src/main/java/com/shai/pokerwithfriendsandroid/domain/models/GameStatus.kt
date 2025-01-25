package com.shai.pokerwithfriendsandroid.domain.models

sealed class GameStatus(val displayName: String) {
    object Active : GameStatus("Active")
    object Completed : GameStatus("Completed")
    object Unknown : GameStatus("Unknown")
}

fun String.toGameStatus(): GameStatus {
    return when (this) {
        "Active" -> GameStatus.Active
        "Completed" -> GameStatus.Completed
        else -> GameStatus.Unknown
    }
}
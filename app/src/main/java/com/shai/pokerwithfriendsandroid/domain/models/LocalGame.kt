package com.shai.pokerwithfriendsandroid.domain.models

import com.shai.pokerwithfriendsandroid.data.remote.models.RemoteGame

data class LocalGame(
    val id: String = "",
    val active: Boolean,
    val buyIn: String,
    val players: List<RemoteGame.PlayerPosition>,
    val dateCreated: Long
)
// todo: player position needs to be converted to a local object as well



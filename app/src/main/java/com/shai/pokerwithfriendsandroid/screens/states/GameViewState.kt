package com.shai.pokerwithfriendsandroid.screens.states

import com.shai.pokerwithfriendsandroid.domain.models.LocalGame

sealed class GameViewState {
    object Loading : GameViewState()
    data class Success(val game: LocalGame) : GameViewState()
    data class Error(val message: String) : GameViewState()
}
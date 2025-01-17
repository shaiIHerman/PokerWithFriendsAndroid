package com.shai.pokerwithfriendsandroid.screens.states

import com.shai.pokerwithfriendsandroid.viewmodels.LocalGameWrapper

sealed class GameViewState {
    object Loading : GameViewState()
    data class Success(val game: LocalGameWrapper) : GameViewState()
    data class Error(val message: String) : GameViewState()
}
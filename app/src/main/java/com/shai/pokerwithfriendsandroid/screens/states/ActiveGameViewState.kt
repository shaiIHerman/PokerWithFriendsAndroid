package com.shai.pokerwithfriendsandroid.screens.states

import com.shai.pokerwithfriendsandroid.domain.models.LocalGame

sealed class ActiveGameViewState {
    object Loading : ActiveGameViewState()
    data class Success(val game: LocalGame) : ActiveGameViewState()
    data class Error(val message: String) : ActiveGameViewState()
}
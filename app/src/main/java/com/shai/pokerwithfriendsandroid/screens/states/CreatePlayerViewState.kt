package com.shai.pokerwithfriendsandroid.screens.states

sealed class CreatePlayerViewState {
    object Idle : CreatePlayerViewState()
    object Adding : CreatePlayerViewState()
    object PlayerAdded : CreatePlayerViewState()
    data class Error(val message: String) : CreatePlayerViewState()
}
package com.shai.pokerwithfriendsandroid.screens.states

sealed class CreateTournamentViewState {
    object Idle : CreateTournamentViewState()
    object Adding : CreateTournamentViewState()
    object TournamentAdded : CreateTournamentViewState()
    data class Error(val message: String) : CreateTournamentViewState()
}
package com.shai.pokerwithfriendsandroid.screens.states

import com.shai.pokerwithfriendsandroid.db.local.models.Tournament
import com.shai.pokerwithfriendsandroid.db.remote.models.RemoteGame
import com.shai.pokerwithfriendsandroid.repositories.LocalUser

sealed class TournamentDetailsViewState {
    object Loading : TournamentDetailsViewState()
    data class Idle(val tournament: Tournament, val games: List<RemoteGame?>) :
        TournamentDetailsViewState()

    data class InSession(val tournament: Tournament, val games: List<RemoteGame?>) :
        TournamentDetailsViewState()

    data class NewGame(val players: List<Pair<Boolean, LocalUser>>) : TournamentDetailsViewState()
    data class Error(val message: String) : TournamentDetailsViewState()
}
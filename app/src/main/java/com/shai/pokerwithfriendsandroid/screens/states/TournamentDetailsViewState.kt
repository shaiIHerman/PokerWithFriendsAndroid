package com.shai.pokerwithfriendsandroid.screens.states

import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.domain.repositories.LocalUser

sealed class TournamentDetailsViewState {
    object Loading : TournamentDetailsViewState()
    data class Idle(val tournament: LocalTournament, val games: List<LocalGame?>) :
        TournamentDetailsViewState()

    data class InSession(val tournament: LocalTournament, val games: List<LocalGame?>) :
        TournamentDetailsViewState()

    data class NewGame(val players: List<Pair<Boolean, LocalUser>>) : TournamentDetailsViewState()
    data class Error(val message: String) : TournamentDetailsViewState()
}
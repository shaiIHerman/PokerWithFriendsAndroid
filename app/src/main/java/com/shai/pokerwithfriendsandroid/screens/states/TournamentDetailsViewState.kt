package com.shai.pokerwithfriendsandroid.screens.states

import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.domain.models.LocalUser

sealed class TournamentDetailsViewState {
    object Loading : TournamentDetailsViewState()
    data class Idle(val tournament: LocalTournament) : TournamentDetailsViewState()

    data class InSession(val tournament: LocalTournament) : TournamentDetailsViewState()

    data class NewGame(val players: List<Pair<Boolean, LocalUser>>) : TournamentDetailsViewState()
    data class Error(val message: String) : TournamentDetailsViewState()
}
package com.shai.pokerwithfriendsandroid.screens.states

import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.domain.models.LocalUser
import com.shai.pokerwithfriendsandroid.utils.Event

sealed class TournamentDetailsViewState {
    object Loading : TournamentDetailsViewState()
    data class CreatingGame(val tournament: LocalTournament): TournamentDetailsViewState()
    data class Idle(val tournament: LocalTournament) : TournamentDetailsViewState()
    data class InSession(val tournament: LocalTournament) : TournamentDetailsViewState()
    data class NewGame(val tournament: LocalTournament) : TournamentDetailsViewState()
    data class GameCreated(val gameId: Event<String>) : TournamentDetailsViewState()
    data class Error(val message: String) : TournamentDetailsViewState()
}
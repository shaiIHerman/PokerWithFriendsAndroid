package com.shai.pokerwithfriendsandroid.screens.states

import com.shai.pokerwithfriendsandroid.data.local.db.entities.TournamentEntity
import com.shai.pokerwithfriendsandroid.data.remote.models.RemoteGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.domain.repositories.LocalUser

sealed class TournamentDetailsViewState {
    object Loading : TournamentDetailsViewState()
    data class Idle(val tournament: LocalTournament, val games: List<RemoteGame?>) :
        TournamentDetailsViewState()

    data class InSession(val tournament: LocalTournament, val games: List<RemoteGame?>) :
        TournamentDetailsViewState()

    data class NewGame(val players: List<Pair<Boolean, LocalUser>>) : TournamentDetailsViewState()
    data class Error(val message: String) : TournamentDetailsViewState()
}
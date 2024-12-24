package com.shai.pokerwithfriendsandroid.screens.states

import com.shai.pokerwithfriendsandroid.data.local.db.entities.TournamentEntity

sealed class TournamentsViewState {
    object Loading : TournamentsViewState()
    data class Success(val tournaments: List<TournamentEntity>) : TournamentsViewState()
    data class Error(val message: String) : TournamentsViewState()
}
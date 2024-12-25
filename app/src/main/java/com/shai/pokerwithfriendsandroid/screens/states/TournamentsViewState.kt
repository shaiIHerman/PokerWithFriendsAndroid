package com.shai.pokerwithfriendsandroid.screens.states

import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament

sealed class TournamentsViewState {
    object Loading : TournamentsViewState()
    data class Success(val tournaments: List<LocalTournament>) : TournamentsViewState()
    data class Error(val message: String) : TournamentsViewState()
}
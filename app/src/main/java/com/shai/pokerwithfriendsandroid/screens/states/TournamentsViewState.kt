package com.shai.pokerwithfriendsandroid.screens.states

import com.shai.pokerwithfriendsandroid.db.local.models.Tournament

sealed class TournamentsViewState {
    object Loading : TournamentsViewState()
    data class Success(val tournaments: List<Tournament>) : TournamentsViewState()
    data class Error(val message: String) : TournamentsViewState()
}
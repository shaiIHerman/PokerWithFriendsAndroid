package com.shai.pokerwithfriendsandroid.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shai.pokerwithfriendsandroid.domain.repositories.TournamentRepository
import com.shai.pokerwithfriendsandroid.screens.states.TournamentsViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TournamentsViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository
) : ViewModel() {

    private val _state = MutableStateFlow<TournamentsViewState>(TournamentsViewState.Loading)
    val state: StateFlow<TournamentsViewState> = _state

    fun fetchTournaments() {
        _state.value = TournamentsViewState.Loading
        viewModelScope.launch {
            tournamentRepository.getTournaments().onSuccess {
                _state.value = TournamentsViewState.Success(it)
            }.onFailure {
                Log.e("TournamentsViewModel", "Error fetching tournaments", it)
                _state.value = TournamentsViewState.Error("Failed to load tournaments")
            }
        }
    }
}

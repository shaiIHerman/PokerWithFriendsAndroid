package com.shai.pokerwithfriendsandroid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament
import com.shai.pokerwithfriendsandroid.repositories.TournamentRepository
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

    init {
        fetchTournaments()
    }

    private fun fetchTournaments() {
        viewModelScope.launch {
            try {
                _state.value = TournamentsViewState.Loading
                val tournaments = tournamentRepository.getTournaments()
                _state.value = TournamentsViewState.Success(tournaments)
            } catch (e: Exception) {
                _state.value = TournamentsViewState.Error("Failed to load tournaments")
            }
        }
    }

    fun addTournament(tournament: Tournament) {
        viewModelScope.launch {
            try {
                // Insert the new tournament into the repository (both local and remote)
                tournamentRepository.addTournament(tournament)
                fetchTournaments() // Refresh the tournament list
            } catch (e: Exception) {
                _state.value = TournamentsViewState.Error("Failed to add tournament")
            }
        }
    }
}

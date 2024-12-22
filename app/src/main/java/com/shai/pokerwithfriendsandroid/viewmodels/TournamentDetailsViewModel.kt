package com.shai.pokerwithfriendsandroid.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament
import com.shai.pokerwithfriendsandroid.repositories.GamesRepository
import com.shai.pokerwithfriendsandroid.repositories.TournamentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TournamentDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tournamentRepository: TournamentRepository,
    private val gamesRepository: GamesRepository
) : ViewModel() {
    private val tournamentId: String? = savedStateHandle["tournamentId"]

    private val _tournament = MutableLiveData<Tournament?>()
    val tournament: LiveData<Tournament?> = _tournament

    init {
        tournamentId?.let {
            // If tournamentId is available, fetch the tournament data using this id
            loadTournamentById(it)
        }
    }

    private fun loadTournamentById(id: String) {
        viewModelScope.launch {
            tournamentRepository.getTournamentById(id).onSuccess { _tournament.value = it }
                .onFailure {
                    //todo: handle error
                }

        }
    }

    fun startNewGame() = viewModelScope.launch {
        _tournament.value?.let { gamesRepository.addGame(it).onSuccess {game ->
            tournamentRepository.addGameToTournament(game, tournamentId!!).onSuccess {
            }
        }
            .onFailure {
                //todo: handle error
            } }
    }
}

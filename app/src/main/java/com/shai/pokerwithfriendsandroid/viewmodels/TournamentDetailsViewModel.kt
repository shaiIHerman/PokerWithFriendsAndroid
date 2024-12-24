package com.shai.pokerwithfriendsandroid.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament
import com.shai.pokerwithfriendsandroid.db.remote.models.RemoteGame
import com.shai.pokerwithfriendsandroid.db.remote.models.User
import com.shai.pokerwithfriendsandroid.repositories.GamesRepository
import com.shai.pokerwithfriendsandroid.repositories.LocalUser
import com.shai.pokerwithfriendsandroid.repositories.TournamentRepository
import com.shai.pokerwithfriendsandroid.repositories.UserRepository
import com.shai.pokerwithfriendsandroid.screens.states.TournamentDetailsViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TournamentDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tournamentRepository: TournamentRepository,
    private val gamesRepository: GamesRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private lateinit var _players: List<Pair<Boolean, LocalUser>>
    private lateinit var _removedPlayers: MutableMap<DocumentReference, User>
    private val tournamentId: String? = savedStateHandle["tournamentId"]

    private val _tournament = MutableLiveData<Tournament?>()
    val tournament: LiveData<Tournament?> = _tournament

    private val _games = MutableLiveData<List<RemoteGame?>>()
    val games: LiveData<List<RemoteGame?>> = _games

    private val _gameInSession = MutableLiveData<Boolean>(false)

    private val _tournamentDetailsUiState =
        MutableLiveData<TournamentDetailsViewState>(TournamentDetailsViewState.Loading)
    val tournamentDetailsUiState: LiveData<TournamentDetailsViewState> = _tournamentDetailsUiState

    init {
        tournamentId?.let { loadTournamentById(it) }
    }

    private fun loadTournamentById(id: String) {
        viewModelScope.launch {
            tournamentRepository.getTournamentById(id).onSuccess {
                _tournament.value = it
                loadGames(_tournament.value?.gameIds ?: emptyList())
                loadUsers(_tournament.value?.playerIds ?: emptyList())
            }.onFailure {
                //todo: handle error
            }

        }
    }

    private fun loadUsers(strings: List<String>) = viewModelScope.launch {
        userRepository.fetchUsersByIds(strings).onSuccess {
            _players = it.map { localUser -> Pair(false, localUser) }
            Log.d("TournamentDetailsViewModel", "Users count: ${it.size}")
        }.onFailure {
            Log.e("TournamentDetailsViewModel", "Error loading users", it)
        }
    }

    private fun loadGames(gameIds: List<String>) = viewModelScope.launch {
        if (gameIds.isEmpty() || gameIds[0].isEmpty()) {
            _tournamentDetailsUiState.value =
                TournamentDetailsViewState.Idle(_tournament.value!!, emptyList())
            return@launch
        }
        gamesRepository.getGamesByIds(gameIds).onSuccess {
            Log.d("TournamentDetailsViewModel", "Games count: ${it.size}")
            _games.value = it
            updateSessionState()
        }.onFailure {
            Log.e("TournamentDetailsViewModel", "Error loading games", it)
        }
    }

    private fun updateSessionState() {
        if (_games.value != null && _games.value!![_games.value!!.size - 1]?.active == true) {
            _tournamentDetailsUiState.value =
                TournamentDetailsViewState.InSession(_tournament.value!!, _games.value!!)
        } else {
            _tournamentDetailsUiState.value =
                TournamentDetailsViewState.Idle(_tournament.value!!, _games.value!!)
        }
    }

    fun startNewGame() = viewModelScope.launch {
        val playersToAdd = _players.filter { !it.first }.map { it.second.id }
        _tournament.value?.let {
            gamesRepository.addGame(it, playersToAdd).onSuccess { game ->
                tournamentRepository.addGameToTournament(game, tournamentId!!).onSuccess {}
                    .onFailure {
                        Log.e("TournamentDetailsViewModel", "Error adding game to tournament", it)
                    }
            }.onFailure {
                Log.e("TournamentDetailsViewModel", "Error adding game", it)
            }
        }
    }

    fun addPlayers() {
        _tournamentDetailsUiState.value = TournamentDetailsViewState.NewGame(_players)
    }

    fun onBackClicked() {
        updateSessionState()
    }

    fun onPlayerSelected(player: TournamentData.AddPlayer) {
            val index = _players.indexOfFirst { it.second.email == player.email }
            if (index != -1) {
                _players = _players.toMutableList().apply {
                    val updatedPlayer = _players[index].copy(first = !_players[index].first) // Update the boolean value
                    this[index] = updatedPlayer // Set the updated player back to the list
                }
                addPlayers()
            }
    }
}

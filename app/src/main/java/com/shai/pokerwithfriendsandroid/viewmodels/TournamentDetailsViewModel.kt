package com.shai.pokerwithfriendsandroid.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shai.pokerwithfriendsandroid.domain.models.GameStatus
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.domain.models.LocalUser
import com.shai.pokerwithfriendsandroid.domain.models.updatePlayersAndGames
import com.shai.pokerwithfriendsandroid.domain.repositories.GamesRepository
import com.shai.pokerwithfriendsandroid.domain.repositories.TournamentRepository
import com.shai.pokerwithfriendsandroid.domain.repositories.UserRepository
import com.shai.pokerwithfriendsandroid.screens.states.TournamentDetailsViewState
import com.shai.pokerwithfriendsandroid.utils.ApiOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TournamentDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tournamentRepository: TournamentRepository,
    private val gamesRepository: GamesRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val tournamentId: String? = savedStateHandle["tournamentId"]

    private val _tournament = MutableLiveData<LocalTournament?>()
    val tournament: LiveData<LocalTournament?> = _tournament

    private val _tournamentDetailsUiState =
        MutableLiveData<TournamentDetailsViewState>(TournamentDetailsViewState.Loading)
    val tournamentDetailsUiState: LiveData<TournamentDetailsViewState> = _tournamentDetailsUiState

    init {
        tournamentId?.let { loadTournamentById(it) }
    }

    private fun loadTournamentById(id: String) {
        viewModelScope.launch {
            tournamentRepository.getTournamentById(id).onSuccess { localTournament ->
                _tournament.value = localTournament

                // Launch loading of games and users in parallel
                val gamesDeferred = async { loadGames(_tournament.value?.gameIds ?: emptyList()) }
                val usersDeferred = async { loadUsers(_tournament.value?.playerIds ?: emptyList()) }

                // Await for both to finish
                try {
                    val games = gamesDeferred.await() // Wait for games to be loaded
                    val users = usersDeferred.await() // Wait for users to be loaded

                    // Only update the UI when both are done
                    _tournament.value!!.updatePlayersAndGames(users, games)
                    _tournament.value?.let { tournament ->
                        tournament.games = games
                        updateSessionState()
                    }

                } catch (e: Exception) {
                    Log.e("TournamentDetailsViewModel", "Error loading games or users", e)
                }
            }.onFailure {
                Log.e("TournamentDetailsViewModel", "Error loading tournament", it)
            }
        }
    }

    private suspend fun loadUsers(playerIds: List<String>): List<LocalUser> {
        return when (val result = userRepository.fetchUsersByIds(playerIds)) {
            is ApiOperation.Success -> result.data // Return the fetched users
            is ApiOperation.Failure -> {
                Log.e("TournamentDetailsViewModel", "Error loading users", result.exception)
                emptyList()
            }
        }
    }

    private suspend fun loadGames(gameIds: List<String>): List<LocalGame> {
        if (gameIds.isEmpty() || gameIds[0].isEmpty()) {
            return emptyList()
        }
        return when (val result = gamesRepository.getGamesByIds(gameIds)) {
            is ApiOperation.Success -> result.data // Return the fetched games
            is ApiOperation.Failure -> {
                Log.e("TournamentDetailsViewModel", "Error loading games", result.exception)
                emptyList()
            }
        }
    }

    private fun updateSessionState() {
        val games = _tournament.value?.games
        if (!games.isNullOrEmpty() && games.last().status == GameStatus.Active) {
            _tournamentDetailsUiState.value = TournamentDetailsViewState.InSession(_tournament.value!!)
        } else {
            _tournamentDetailsUiState.value = TournamentDetailsViewState.Idle(_tournament.value!!)
        }
    }


    fun startNewGame() = viewModelScope.launch {
        val playersToAdd = _tournament.value?.players?.filter { !it.first }?.map { it.second.id }
        _tournament.value?.let {
            gamesRepository.addGame(it, playersToAdd!!).onSuccess { game ->
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
        _tournamentDetailsUiState.value =
            TournamentDetailsViewState.NewGame(_tournament.value!!.players)
    }

    fun onBackClicked() {
        updateSessionState()
    }

    fun onPlayerSelected(player: TournamentData.AddPlayer) {
        val players = _tournament.value!!.players
        val index = players.indexOfFirst { it.second.email == player.email }
        if (index != -1) {
            _tournament.value!!.players = players.toMutableList().apply {
                val updatedPlayer =
                    players[index].copy(first = !players[index].first) // Update the boolean value
                this[index] = updatedPlayer // Set the updated player back to the list
            }
            addPlayers()
        }
    }
}

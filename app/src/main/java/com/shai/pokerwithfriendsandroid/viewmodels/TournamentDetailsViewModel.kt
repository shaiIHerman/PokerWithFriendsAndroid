package com.shai.pokerwithfriendsandroid.viewmodels

import android.util.Log
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _tournamentDetailsUiState =
        MutableStateFlow<TournamentDetailsViewState>(TournamentDetailsViewState.Loading)
    val tournamentDetailsUiState = _tournamentDetailsUiState.asStateFlow()

    init {
        tournamentId?.let { loadTournamentById(it) }
    }

    private fun loadTournamentById(id: String) {
        viewModelScope.launch {
            tournamentRepository.getTournamentById(id).onSuccess { localTournament ->

                // Launch loading of games and users in parallel
                val gamesDeferred = async { loadGames(localTournament.gameIds) }
                val usersDeferred = async { loadUsers(localTournament.playerIds) }

                try {
                    val games = gamesDeferred.await() // Wait for games to be loaded
                    val users = usersDeferred.await() // Wait for users to be loaded

                    localTournament.updatePlayersAndGames(users, games)
                    updateSessionState(localTournament)
                } catch (e: Exception) {
                    Log.e("TournamentDetailsViewModel", "Error loading games or users", e)
                }
            }.onFailure {
                Log.e("TournamentDetailsViewModel", "Error loading tournament", it)
            }
        }
    }

    fun startNewGame() = viewModelScope.launch {
        val tournament = getLocalTournament(_tournamentDetailsUiState.value)!!
        _tournamentDetailsUiState.value = TournamentDetailsViewState.CreatingGame(tournament)
        val playersToAdd = tournament.players.filter { !it.first }.map { it.second.id }
        tournament.let {
            gamesRepository.addGame(it, playersToAdd).onSuccess { game ->
                tournamentRepository.addGameToTournament(game, tournamentId!!).onSuccess {
                    _tournamentDetailsUiState.value = TournamentDetailsViewState.GameCreated(game)
                }.onFailure {
                    Log.e("TournamentDetailsViewModel", "Error adding game to tournament", it)
                }
            }.onFailure {
                Log.e("TournamentDetailsViewModel", "Error adding game", it)
            }
        }
    }

    fun addPlayers() {
        _tournamentDetailsUiState.update { currentState ->
            return@update TournamentDetailsViewState.NewGame(getLocalTournament(currentState)!!)
        }
    }

    fun onBackClicked() {
        updateSessionState(getLocalTournament(_tournamentDetailsUiState.value)!!)
    }

    fun onPlayerSelected(player: TournamentData.AddPlayer) {
        _tournamentDetailsUiState.update { currentState ->
            val localTournament =
                getLocalTournament(currentState)?.copy() // Create a new copy of the tournament to ensure immutability
                    ?: return@update currentState

            val players = localTournament.players.toMutableList()
            val index = players.indexOfFirst { it.second.email == player.email }

            if (index != -1) {
                players[index] = players[index].copy(first = !players[index].first)

                // Create a new instance otherwise it will be treated as the same object and not collected
                val updatedTournament = localTournament.copy(players = players)

                return@update TournamentDetailsViewState.NewGame(updatedTournament)
            } else {
                return@update currentState
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

    private suspend fun loadUsers(playerIds: List<String>): List<LocalUser> {
        return when (val result = userRepository.fetchUsersByIds(playerIds)) {
            is ApiOperation.Success -> result.data // Return the fetched users
            is ApiOperation.Failure -> {
                Log.e("TournamentDetailsViewModel", "Error loading users", result.exception)
                emptyList()
            }
        }
    }

    private fun updateSessionState(tournament: LocalTournament) {
        val games = tournament.games
        if (games.isNotEmpty() && games.last().status == GameStatus.Active) {
            _tournamentDetailsUiState.value = TournamentDetailsViewState.InSession(tournament)
        } else {
            _tournamentDetailsUiState.value = TournamentDetailsViewState.Idle(tournament)
        }
    }

    private fun getLocalTournament(currentState: TournamentDetailsViewState): LocalTournament? {
        return when (currentState) {
            is TournamentDetailsViewState.Idle -> currentState.tournament
            is TournamentDetailsViewState.InSession -> currentState.tournament
            is TournamentDetailsViewState.NewGame -> currentState.tournament
            else -> null
        }
    }
}

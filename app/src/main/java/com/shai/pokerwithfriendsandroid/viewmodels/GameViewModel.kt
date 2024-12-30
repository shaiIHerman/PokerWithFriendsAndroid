package com.shai.pokerwithfriendsandroid.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.remote.FirestoreRealtimeListener
import com.shai.pokerwithfriendsandroid.data.remote.models.RemoteGame
import com.shai.pokerwithfriendsandroid.data.remote.models.toLocalGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalUser
import com.shai.pokerwithfriendsandroid.domain.repositories.GamesRepository
import com.shai.pokerwithfriendsandroid.screens.states.GameViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gamesRepository: GamesRepository,
    private val firestoreClient: FireStoreClient
) : ViewModel() {
    private val gameId: String? = savedStateHandle["gameId"]
    private val _gameDetailsUiState = MutableLiveData<GameViewState>(GameViewState.Loading)
    val gameDetailsUiState: LiveData<GameViewState> = _gameDetailsUiState
    private val firestoreListener = FirestoreRealtimeListener()

    init {
        startListeningForGameUpdates()
    }

    private fun startListeningForGameUpdates() = viewModelScope.launch {
        firestoreListener.listenToDocumentChanges<RemoteGame>(gameId!!).collect { remoteGame ->
            val players = remoteGame?.players?.map { player ->
                firestoreClient.getUserByDocReference(player.player!!)
            }
            Log.d("FirestoreListener", "Document snapshot: ${remoteGame?.id}")
            _gameDetailsUiState.value = GameViewState.Success(remoteGame?.toLocalGame(players)!!)
        }
    }

    fun onPlayerLost(localUser: LocalUser) {
        val currentGame = (_gameDetailsUiState.value as? GameViewState.Success)?.game ?: return
        val playerPositions = currentGame.players

        val newPosition = calculateNewPosition(playerPositions)

        val updatedPlayers = updatePlayerPositions(playerPositions, localUser.id, newPosition)
        var gameOver = false
        if (newPosition == 2) { // We have a winner
            gameOver = true
            updatedPlayers.find { it.position == 0 }?.let {
                val updatedList = updatedPlayers.map { player ->
                    if (player.position == 0) player.copy(position = 1) else player
                }
                currentGame.players = updatedList
            }
        } else {
            currentGame.players = updatedPlayers
        }

        viewModelScope.launch {
            gamesRepository.updatePlayerPositions(game = currentGame, gameOver = gameOver).onSuccess {
                Log.d("GameViewModel", "Player removed successfully")
            }.onFailure {
                Log.e("GameViewModel", "Error removing player: ${it.message}")
            }
        }
    }

    private fun calculateNewPosition(playerPositions: List<LocalGame.PlayerPosition>): Int {
        val nonZeroPositions = playerPositions.map { it.position }.filter { it > 0 }
        return if (nonZeroPositions.isEmpty()) playerPositions.size else nonZeroPositions.min() - 1
    }

    private fun updatePlayerPositions(
        playerPositions: List<LocalGame.PlayerPosition>,
        removedPlayerId: String,
        newPosition: Int
    ): List<LocalGame.PlayerPosition> {
        return playerPositions
            .map { playerPosition ->
                if (playerPosition.player?.id == removedPlayerId) {
                    playerPosition.copy(position = newPosition)
                } else {
                    playerPosition
                }
            }
            .sortedBy { it.position }
    }

}

package com.shai.pokerwithfriendsandroid.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalUser
import com.shai.pokerwithfriendsandroid.domain.repositories.GamesRepository
import com.shai.pokerwithfriendsandroid.screens.states.GameViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocalGameWrapper(val localGame: LocalGame) {
    override fun equals(other: Any?): Boolean {
        val equals = other is LocalGameWrapper && other.localGame == this.localGame
        val otherGame = (other as LocalGameWrapper).localGame
        val newPositions = this.localGame.players.map { it.position }
        val otherPositions = otherGame.players.map { it.position }
        Log.d("LocalGameWrapper", "${newPositions}")
        Log.d("LocalGameWrapper", "${otherPositions}")
        Log.d("LocalGameWrapper", "${newPositions == otherPositions}")
        return equals
    }
}

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, private val gamesRepository: GamesRepository
) : ViewModel() {
    private val gameId: String? = savedStateHandle["gameId"]
    private val _gameDetailsUiState = MutableStateFlow<GameViewState>(GameViewState.Loading)
    val gameDetailsUiState = _gameDetailsUiState.asStateFlow()

    init {
        startListeningForGameUpdates()
    }

    private fun startListeningForGameUpdates() = viewModelScope.launch {
        gamesRepository.listenForGameUpdates(gameId!!).collect { newGame ->
            if (_gameDetailsUiState.value is GameViewState.Success) {
                Log.d("GameViewModel", "Game updated: $newGame")
                val oldGame = (_gameDetailsUiState.value as? GameViewState.Success)?.game?.localGame
                Log.d("GameViewModel", "Game updated: $newGame")
                Log.d("GameViewModel", "oldGame: $oldGame")
                val equals = newGame == oldGame
            }
            _gameDetailsUiState.update { currentState ->
                Log.d("GameViewModeld", "Game updated: $newGame")
                Log.d("GameViewModeld", "oldGame: ${(currentState as? GameViewState.Success)?.game?.localGame}")
                return@update GameViewState.Success(LocalGameWrapper(newGame))
            }
    }
}

fun onPlayerLost(localUser: LocalUser) {
    val currentGame =
        (_gameDetailsUiState.value as? GameViewState.Success)?.game?.localGame ?: return
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
    playerPositions: List<LocalGame.PlayerPosition>, removedPlayerId: String, newPosition: Int
): List<LocalGame.PlayerPosition> {
    return playerPositions.map { playerPosition ->
        if (playerPosition.player?.id == removedPlayerId) {
            playerPosition.copy(position = newPosition)
        } else {
            playerPosition
        }
    }.sortedBy { it.position }
}

}

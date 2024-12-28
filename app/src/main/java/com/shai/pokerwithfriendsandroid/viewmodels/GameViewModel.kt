package com.shai.pokerwithfriendsandroid.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalUser
import com.shai.pokerwithfriendsandroid.domain.repositories.GamesRepository
import com.shai.pokerwithfriendsandroid.screens.states.GameViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, private val gamesRepository: GamesRepository
) : ViewModel() {
    private val gameId: String? = savedStateHandle["gameId"]
    private lateinit var gameCache: LocalGame
    private val _gameDetailsUiState = MutableLiveData<GameViewState>(GameViewState.Loading)
    val gameDetailsUiState: LiveData<GameViewState> = _gameDetailsUiState

    init {
        gameId?.let { loadGameById(it) }
    }

    private fun loadGameById(gameId: String) = viewModelScope.launch {
        gamesRepository.getGameById(gameId).onSuccess { game ->
            game?.let {
                gameCache = game
                _gameDetailsUiState.value = GameViewState.Success(game)
            }
        }.onFailure { error ->
            Log.e("GameViewModel", "Error loading game by ID: ${error.message}")
        }
    }

    fun removePlayer(localUser: LocalUser) {

    }
}

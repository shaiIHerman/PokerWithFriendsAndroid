package com.shai.pokerwithfriendsandroid.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shai.pokerwithfriendsandroid.domain.repositories.GamesRepository
import com.shai.pokerwithfriendsandroid.screens.states.ActiveGameViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveGameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, private val gamesRepository: GamesRepository
) : ViewModel() {
    private val gameId: String? = savedStateHandle["gameId"]
    private val _gameDetailsUiState =
        MutableLiveData<ActiveGameViewState>(ActiveGameViewState.Loading)
    val gameDetailsUiState: LiveData<ActiveGameViewState> = _gameDetailsUiState

    init {
        gameId?.let { loadGameById(it) }
    }

    private fun loadGameById(gameId: String) = viewModelScope.launch {
        gamesRepository.getGameById(gameId).onSuccess { game ->
            _gameDetailsUiState.value = ActiveGameViewState.Success(game!!)
        }.onFailure { error ->
            Log.e("ActiveGameViewModel", "Error loading game by ID: ${error.message}")
        }
    }
}

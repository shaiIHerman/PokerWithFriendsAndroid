package com.shai.pokerwithfriendsandroid.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shai.pokerwithfriendsandroid.db.remote.models.User
import com.shai.pokerwithfriendsandroid.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPlayersViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    sealed interface SearchState {
        object Empty : SearchState
        data class UserQuery(val query: String) : SearchState
    }

    sealed interface ScreenState {
        object Empty : ScreenState
        object Searching : ScreenState
        data class Error(val message: String) : ScreenState
        data class Content(val results: List<User>) : ScreenState
    }

    val searchTextFieldState = TextFieldState()
    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Empty)
    val uiState: StateFlow<ScreenState> = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val searchTextState: StateFlow<SearchState> =
        snapshotFlow { searchTextFieldState.text }.debounce(500)
            .mapLatest { if (it.isBlank()) SearchState.Empty else SearchState.UserQuery(it.toString()) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 2000),
                initialValue = SearchState.Empty
            )

    fun observeUserSearch() = viewModelScope.launch {
        searchTextState.collectLatest { searchState ->
            when (searchState) {
                SearchState.Empty -> _uiState.update { ScreenState.Empty }
                is SearchState.UserQuery -> {
                    searchAllCharacters(searchState.query)
                }
            }
        }
    }

    private fun searchAllCharacters(query: String) = viewModelScope.launch {
        _uiState.update { ScreenState.Searching }
        userRepository.fetchUsersByName(searchQuery = query).onSuccess { users ->
            _uiState.update {
                ScreenState.Content(
                    results = users,
                )
            }
        }.onFailure { _uiState.update { ScreenState.Error("No search results found") } }
    }
}
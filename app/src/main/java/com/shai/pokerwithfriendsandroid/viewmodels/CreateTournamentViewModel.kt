package com.shai.pokerwithfriendsandroid.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import com.shai.pokerwithfriendsandroid.repositories.TournamentRepository
import com.shai.pokerwithfriendsandroid.repositories.UserRepository
import com.shai.pokerwithfriendsandroid.screens.states.CreatePlayerViewState
import com.shai.pokerwithfriendsandroid.screens.states.CreateTournamentViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTournamentViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tournamentRepository: TournamentRepository
) : ViewModel() {

    private val _tournament = MutableLiveData(TournamentData())
    val tournament: LiveData<TournamentData> = _tournament

    private val _createPlayerUiState =
        MutableLiveData<CreatePlayerViewState>(CreatePlayerViewState.Idle)
    val createPlayerUiState: LiveData<CreatePlayerViewState> = _createPlayerUiState

    private val _createTournamentUiState =
        MutableLiveData<CreateTournamentViewState>(CreateTournamentViewState.Idle)
    val createTournamentUiState: LiveData<CreateTournamentViewState> = _createTournamentUiState

    fun updateTournamentName(tournamentName: String) {
        _tournament.value = _tournament.value?.copy(name = tournamentName)
    }

    fun updateTournamentBuyIn(buyIn: String) {
        _tournament.value = _tournament.value?.copy(buyIn = buyIn)
    }

    fun updateTournamentPlayers(player: TournamentData.AddPlayer) {
        _tournament.value =
            _tournament.value?.copy(players = _tournament.value?.players?.plus(player))
        _createPlayerUiState.value = CreatePlayerViewState.PlayerAdded
        viewModelScope.launch {
            delay(1000)
            _createPlayerUiState.value = CreatePlayerViewState.Idle
        }
    }

    fun createPlayer(newUser: TournamentData.AddPlayer) = viewModelScope.launch {
        _createPlayerUiState.value = CreatePlayerViewState.Adding
        userRepository.createPlayer(newUser.name, newUser.email).onSuccess {
            newUser.documentReference = it
            updateTournamentPlayers(newUser)

        }.onFailure {
            _createPlayerUiState.value = CreatePlayerViewState.Error(it.message ?: "Unknown error")
        }
    }

    fun getCurrentUserRef(): DocumentReference? {
        return userRepository.getUserRef()
    }

    fun createTournament() = viewModelScope.launch {
        _createTournamentUiState.value = CreateTournamentViewState.Adding
        val userRef = getCurrentUserRef() ?: run {
            Log.e("CreateTournamentViewModel", "User reference is not available")
            return@launch
        }
        val updatedPlayers = _tournament.value?.players?.map { player ->
            if (player.documentReference == null) {
                userRepository.getUserByEmail(player.email).onSuccess {
                    player.documentReference = it
                }.onFailure {
                    Log.e(
                        "CreateTournamentViewModel", "Error fetching user by email: ${it.message}"
                    )
                }
            }
            player
        }
        val filteredPlayers = updatedPlayers?.filter { it.documentReference != null }
        _tournament.value = _tournament.value?.copy(players = filteredPlayers)
        _tournament.value = _tournament.value?.copy(admin = userRef)

        tournamentRepository.addTournament(_tournament.value!!).onSuccess {
            _createTournamentUiState.value = CreateTournamentViewState.TournamentAdded
        }.onFailure {
            _createTournamentUiState.value =
                CreateTournamentViewState.Error(it.message ?: "Unknown error")
        }
    }
}

data class TournamentData(
    val name: String = "",
    val buyIn: String = "",
    val players: List<AddPlayer>? = emptyList(),
    val admin: DocumentReference? = null
) {
    class AddPlayer(
        val name: String, val email: String = "", var documentReference: DocumentReference? = null
    )
}
package com.shai.pokerwithfriendsandroid.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament
import com.shai.pokerwithfriendsandroid.repositories.TournamentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TournamentDetailsViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository, savedStateHandle: SavedStateHandle
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
}

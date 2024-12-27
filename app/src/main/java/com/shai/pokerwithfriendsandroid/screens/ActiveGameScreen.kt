package com.shai.pokerwithfriendsandroid.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shai.pokerwithfriendsandroid.components.AppTopBar
import com.shai.pokerwithfriendsandroid.components.LoadingState
import com.shai.pokerwithfriendsandroid.components.PrimaryButton
import com.shai.pokerwithfriendsandroid.components.ShowUsers
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.domain.repositories.LocalUser
import com.shai.pokerwithfriendsandroid.screens.states.ActiveGameViewState
import com.shai.pokerwithfriendsandroid.screens.states.TournamentDetailsViewState
import com.shai.pokerwithfriendsandroid.viewmodels.ActiveGameViewModel
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentData
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentDetailsViewModel

@Composable
fun ActiveGameScreen(viewModel: ActiveGameViewModel) {
    val gameDetailsViewState by viewModel.gameDetailsUiState.observeAsState(
        ActiveGameViewState.Loading
    )
    Scaffold(
//        topBar = {
//            TournamentDetailsTopBar(uiState = tournamentDetailsViewState) { viewModel.onBackClicked() }
//        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            when (val state = gameDetailsViewState) {
                ActiveGameViewState.Loading -> LoadingState()
                is ActiveGameViewState.Success -> GameDetailsContent(state.game)
                is ActiveGameViewState.Error -> TODO()
            }
        }
    }
}

@Composable
fun GameDetailsContent(
    game: LocalGame,
//    bottomButton: @Composable () -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
//        Text("Tournament Name: ${tournament.name}")
        Text("Buy-In: ${game.buyIn}")
        Text("No. of players: ${game.players.size}")
//        Text("No. of games played: $gamesPlayed")
        Spacer(modifier = Modifier.weight(1f))
//        bottomButton()
    }
}
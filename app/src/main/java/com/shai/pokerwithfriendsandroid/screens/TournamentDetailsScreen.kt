package com.shai.pokerwithfriendsandroid.screens

import android.util.Log
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
import com.shai.pokerwithfriendsandroid.components.AppTopBar
import com.shai.pokerwithfriendsandroid.components.LoadingState
import com.shai.pokerwithfriendsandroid.components.PrimaryButton
import com.shai.pokerwithfriendsandroid.components.ShowUsers
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.domain.repositories.LocalUser
import com.shai.pokerwithfriendsandroid.screens.states.TournamentDetailsViewState
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentData
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentDetailsViewModel

@Composable
fun TournamentDetailsScreen(viewModel: TournamentDetailsViewModel, onNavigateToGame: (String?) -> Unit) {
    val tournamentDetailsViewState by viewModel.tournamentDetailsUiState.observeAsState(
        TournamentDetailsViewState.Loading
    )

    val games by viewModel.games.observeAsState()

    Scaffold(
        topBar = {
            TournamentDetailsTopBar(uiState = tournamentDetailsViewState) { viewModel.onBackClicked() }
        }, containerColor = Color.Transparent
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            when (val state = tournamentDetailsViewState) {
                TournamentDetailsViewState.Loading -> LoadingState()
                is TournamentDetailsViewState.Idle -> {
                    TournamentDetailsContent(
                        state.tournament
                    ) {
                        BottomButton("Start New Game") {
                            viewModel.addPlayers()
                        }
                    }
                }

                is TournamentDetailsViewState.InSession -> TournamentDetailsContent(
                    state.tournament
                ) {
                    BottomButton("Game In Session ->") {
                        Log.d("TournamentDetailsScreen", "Games: ${games?.last()?.id}")
                        onNavigateToGame(games?.last()?.id)
                    }
                }

                is TournamentDetailsViewState.Error -> TODO()
                is TournamentDetailsViewState.NewGame -> AddPLayersToGame(state.players,
                    onPlayerSelected = {
                        viewModel.onPlayerSelected(it)
                    },
                    onConfirmClicked = {
                        viewModel.startNewGame()
                    })
            }
        }
    }
}

@Composable
fun TournamentDetailsTopBar(uiState: TournamentDetailsViewState, onBackClicked: () -> Unit) {
    when (uiState) {
        is TournamentDetailsViewState.NewGame -> {
            AppTopBar(title = "Confirm Players",
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onIconClick = { onBackClicked() })
        }

        else -> {
            AppTopBar(title = "Tournament Details")
        }
    }
}

@Composable
fun AddPLayersToGame(
    players: List<Pair<Boolean, LocalUser>>,
    onPlayerSelected: (TournamentData.AddPlayer) -> Unit,
    onConfirmClicked: () -> Unit
) {
    ShowUsers(players) {
        onPlayerSelected(it)
    }
    BottomButton("Confirm & Start") { onConfirmClicked() }
}

@Composable
fun TournamentDetailsContent(
    tournament: LocalTournament,
    bottomButton: @Composable () -> Unit,
) {
    val gamesPlayed = if (tournament.gameIds[0].isEmpty()) 0 else tournament.gameIds.size
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tournament Name: ${tournament.name}")
        Text("Buy-In: ${tournament.buyIn}")
        Text("No. of players: ${tournament.playerIds.size}")
        Text("No. of games played: $gamesPlayed")
        Spacer(modifier = Modifier.weight(1f))
        bottomButton()
    }
}

@Composable
fun BottomButton(text: String, onClick: () -> Unit) {
    PrimaryButton(text) { onClick() }
}

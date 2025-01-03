package com.shai.pokerwithfriendsandroid.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.shai.pokerwithfriendsandroid.components.AppSpacer
import com.shai.pokerwithfriendsandroid.components.AppTopBar
import com.shai.pokerwithfriendsandroid.components.LoadingState
import com.shai.pokerwithfriendsandroid.components.PrimaryButton
import com.shai.pokerwithfriendsandroid.components.ShowUsers
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.domain.models.LocalUser
import com.shai.pokerwithfriendsandroid.screens.states.TournamentDetailsViewState
import com.shai.pokerwithfriendsandroid.ui.theme.BrandColor
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentData
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentDetailsViewModel

@Composable
fun TournamentDetailsScreen(
    viewModel: TournamentDetailsViewModel,
    onNavigateToGame: (String?) -> Unit,
    onNavigateToStats: (Int) -> Unit,
    onBackClicked: () -> Unit
) {
    val tournamentDetailsViewState by viewModel.tournamentDetailsUiState.observeAsState(
        TournamentDetailsViewState.Loading
    )

    Scaffold(
        topBar = {
            TournamentDetailsTopBar(uiState = tournamentDetailsViewState,
                onNewGameBackClicked = { viewModel.onBackClicked() }) {
                onBackClicked()
            }
        }, containerColor = Color.Transparent
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            when (val state = tournamentDetailsViewState) {
                TournamentDetailsViewState.Loading -> LoadingState()
                is TournamentDetailsViewState.Idle -> {
                    TournamentDetailsContent(state.tournament, onStatsClicked = {
                        onNavigateToStats(it)
                    }) {
                        BottomButton("Start New Game") {
                            viewModel.addPlayers()
                        }
                    }
                }

                is TournamentDetailsViewState.InSession -> TournamentDetailsContent(
                    state.tournament,
                    onStatsClicked = {
                        onNavigateToStats(it)
                    }) {
                    BottomButton("Game In Session ->") {
                        Log.d(
                            "TournamentDetailsScreen", "Games: ${state.tournament.games.last().id}"
                        )
                        onNavigateToGame(state.tournament.games.last().id)
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
fun TournamentDetailsTopBar(
    uiState: TournamentDetailsViewState, onNewGameBackClicked: () -> Unit, onBackClicked: () -> Unit
) {
    when (uiState) {
        is TournamentDetailsViewState.NewGame -> {
            AppTopBar(title = "Confirm Players",
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onIconClick = { onNewGameBackClicked() })
        }

        else -> {
            AppTopBar(title = "Tournament Details",
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onIconClick = { onBackClicked() })
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
    onStatsClicked: (Int) -> Unit,
    bottomButton: @Composable () -> Unit,
) {
    val gamesPlayed = if (tournament.gameIds[0].isEmpty()) 0 else tournament.gameIds.size
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tournament Name: ${tournament.name}")
        Text("Buy-In: ${tournament.buyIn}")
        Text("No. of players: ${tournament.playerIds.size}")
        Text("No. of games played: $gamesPlayed")
        AppSpacer()
        HorizontalDivider()
        AppSpacer()
        Text("Leaders:")
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Position leader: ${tournament.getPositionLeader()}")
            SeeDetails { onStatsClicked(0) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Earnings leader: ${tournament.getMoneyLeader()}")
            SeeDetails { onStatsClicked(1) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("First place leader: ${tournament.getFirstPlaceLeader()}")
            SeeDetails { onStatsClicked(2) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Second place leader: ${tournament.getSecondPlaceLeader()}")
            SeeDetails { onStatsClicked(2) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Bubble leader: ${tournament.getBubbleLeader()}")
            SeeDetails { onStatsClicked(2) }
        }
        Spacer(modifier = Modifier.weight(1f))
        bottomButton()
    }
}

@Composable
fun BottomButton(text: String, onClick: () -> Unit) {
    PrimaryButton(text) { onClick() }
}

@Composable
fun SeeDetails(onClick: () -> Unit) {
    val actionText = "See Details"
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = BrandColor, fontWeight = FontWeight.Bold)) {
            pushStringAnnotation(tag = actionText, annotation = actionText)
            append(actionText)
        }
    }

    ClickableText(text = annotatedString, onClick = {
        annotatedString.getStringAnnotations(it, it).firstOrNull()?.also { span ->
            Log.d("BottomLoginTextComponent", "${span.item} is Clicked")
            onClick()
        }
    })
}

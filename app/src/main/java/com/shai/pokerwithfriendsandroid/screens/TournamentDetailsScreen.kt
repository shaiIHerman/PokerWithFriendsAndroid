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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.shai.pokerwithfriendsandroid.components.SpacerSize
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.domain.models.LocalUser
import com.shai.pokerwithfriendsandroid.screens.states.TournamentDetailsViewState
import com.shai.pokerwithfriendsandroid.ui.theme.BrandColor
import com.shai.pokerwithfriendsandroid.ui.theme.Tertirary
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentData
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentDetailsViewModel

@Composable
fun TournamentDetailsScreen(
    viewModel: TournamentDetailsViewModel,
    onNavigateToGame: (String?) -> Unit,
    onNavigateToStats: (Int) -> Unit,
    onBackClicked: () -> Unit
) {
    val tournamentDetailsViewState by viewModel.tournamentDetailsUiState.collectAsState()

    LaunchedEffect(tournamentDetailsViewState) {
        if (tournamentDetailsViewState is TournamentDetailsViewState.GameCreated) {
            onNavigateToGame((tournamentDetailsViewState as TournamentDetailsViewState.GameCreated).gameId)
        }
    }
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

                is TournamentDetailsViewState.InSession -> TournamentDetailsContent(state.tournament,
                    isInSession = true,
                    onStatsClicked = {
                        onNavigateToStats(it)
                    }) {
                    BottomButton("Game In Session ->") {
                        onNavigateToGame(state.tournament.games.last().id)
                    }
                }

                is TournamentDetailsViewState.Error -> TODO()
                is TournamentDetailsViewState.NewGame -> AddPLayersToGame(state.tournament.players,
                    onPlayerSelected = {
                        viewModel.onPlayerSelected(it)
                    },
                    onConfirmClicked = {
                        viewModel.startNewGame()
                    })

                is TournamentDetailsViewState.CreatingGame -> CreatingGame(state.tournament.players,
                    {},
                    {})

                else -> {}
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
    BottomButton(text = "Confirm & Start") { onConfirmClicked() }
}

@Composable
fun CreatingGame(
    players: List<Pair<Boolean, LocalUser>>,
    onPlayerSelected: (TournamentData.AddPlayer) -> Unit,
    onConfirmClicked: () -> Unit
) {
    ShowUsers(players) {
        onPlayerSelected(it)
    }
    BottomButton(text = "Confirm & Start", progressText = "Creating Game") { onConfirmClicked() }
}

@Composable
fun TournamentDetailsContent(
    tournament: LocalTournament,
    isInSession: Boolean = false,
    onStatsClicked: (Int) -> Unit,
    bottomButton: @Composable () -> Unit
) {
    val gamesPlayed =
        if (tournament.gameIds[0].isEmpty()) 0 else tournament.gameIds.size - if (isInSession) 1 else 0
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tournament Name: ${tournament.name}")
        Text("Buy-In: ${tournament.buyIn}")
        Text("No. of players: ${tournament.playerIds.size}")
        Text("No. of games played: $gamesPlayed")
        AppSpacer()
        HorizontalDivider()
        AppSpacer()
        Text("Leaders:")
        AppSpacer(SpacerSize.Medium)
        if (tournament.games.isEmpty()) {
            Text("No games played yet, no leaders to show")
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Position leader:")
                    Text(tournament.getPositionLeader(), color = Tertirary)
                }
                SeeDetails { onStatsClicked(0) }
            }
            AppSpacer(SpacerSize.Medium)
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Earnings leader:")
                    Text(tournament.getMoneyLeader(), color = Tertirary)
                }
                SeeDetails { onStatsClicked(1) }
            }
            AppSpacer(SpacerSize.Medium)
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text("First place leader:")
                    Text(tournament.getFirstPlaceLeader(), color = Tertirary)
                }
                SeeDetails { onStatsClicked(2) }
            }
            AppSpacer(SpacerSize.Medium)
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Second place leader:")
                    Text(tournament.getSecondPlaceLeader(), color = Tertirary)
                }
                SeeDetails { onStatsClicked(2) }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        bottomButton()
    }
}

@Composable
fun BottomButton(text: String, progressText: String? = null, onClick: () -> Unit) {
    if (progressText == null) {
        PrimaryButton(text) { onClick() }
    } else {
        PrimaryButton(text, showProgress = true, progressText = progressText) { onClick() }
    }
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

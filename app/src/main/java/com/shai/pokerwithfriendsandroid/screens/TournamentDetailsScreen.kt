package com.shai.pokerwithfriendsandroid.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shai.pokerwithfriendsandroid.components.AppTopBar
import com.shai.pokerwithfriendsandroid.components.PrimaryButton
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentDetailsViewModel

@Composable
fun TournamentDetailsScreen(viewModel: TournamentDetailsViewModel) {

//    LaunchedEffect(key1 = Unit) { viewModel.fetchGames() }
    Scaffold(
        topBar = { AppTopBar(title = "Tournament Details") }, containerColor = Color.Transparent
    ) { paddingValues ->
//Next - parse players to tournament local object to use when creating a new game. Create a new game and then add it to the tournament.
        val tournament by viewModel.tournament.observeAsState()
        Column(modifier = Modifier.padding(paddingValues)) {
            tournament?.let {
                TournamentDetailsContent(it){viewModel.startNewGame()}

            } ?: Text("Loading tournament...")
        }
    }
}

@Composable
fun TournamentDetailsContent(tournament: Tournament, onStartNewGame: () -> Unit) {
    val gamesPlayed = if (tournament.gameIds[0].isEmpty()) 0 else tournament.gameIds.size
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tournament Name: ${tournament.name}")
        Text("Buy-In: ${tournament.buyIn}")
        Text("No. of players: ${tournament.playerIds.size}")
        Text("No. of games played: $gamesPlayed")
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton("Start New Game"){
            onStartNewGame()
        }
    }
}

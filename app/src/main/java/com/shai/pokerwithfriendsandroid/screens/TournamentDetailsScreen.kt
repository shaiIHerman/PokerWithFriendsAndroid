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

    // We want to use LaunchedEffect to fetch tournaments only once when the composable is first created,
    // and not from the viewmodel init because we want it to be in sync with the state controlled by
    // the viewmodel but handled by the composable.
//    LaunchedEffect(key1 = Unit) { viewModel.fetchGames() }
    Scaffold(
        topBar = { AppTopBar(title = "Tournament Details") }, containerColor = Color.Transparent
    ) { paddingValues ->

        val tournament by viewModel.tournament.observeAsState()
        Column(modifier = Modifier.padding(paddingValues)) {
            tournament?.let {
                TournamentDetailsContent(it)

            } ?: Text("Loading tournament...")
        }
    }
}

@Composable
fun TournamentDetailsContent(tournament: Tournament) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tournament Name: ${tournament.name}")
        Text("Buy-In: ${tournament.buyIn}")
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton("Start New Game")
    }
}

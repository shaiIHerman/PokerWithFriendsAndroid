package com.shai.pokerwithfriendsandroid.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shai.pokerwithfriendsandroid.components.LoadingState
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament
import com.shai.pokerwithfriendsandroid.screens.states.TournamentsViewState
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: TournamentsViewModel = hiltViewModel(), onAddTournament: () -> Unit) {
    val state by viewModel.state.collectAsState()

    // We want to use LaunchedEffect to fetch tournaments only once when the composable is first created,
    // and not from the viewmodel init because we want it to be in sync with the state controlled by
    // the viewmodel but handled by the composable.
    LaunchedEffect(key1 = Unit) { viewModel.fetchTournaments() }
    Scaffold(topBar = {
        TopAppBar(title = { Text("Tournaments") })
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = { onAddTournament() }
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Tournament")
        }
    }) { paddingValues ->
        when (state) {
            is TournamentsViewState.Loading -> {
                LoadingState()
            }

            is TournamentsViewState.Success -> {
                val tournaments = (state as TournamentsViewState.Success).tournaments
                TournamentList(paddingValues, tournaments)
            }

            is TournamentsViewState.Error -> {
                val message = (state as TournamentsViewState.Error).message
                Column(modifier = Modifier.padding(paddingValues)) {
                    Text(text = "Error: $message", color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun TournamentList(paddingValues: PaddingValues, tournaments: List<Tournament>) {
    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        items(tournaments) { tournament ->
            TournamentItem(tournament)
        }
    }
}

@Composable
fun TournamentItem(tournament: Tournament) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.Cyan, shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = tournament.name,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${tournament.gamesPlayed} games played",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray.copy(alpha = 0.6f)
        )
    }
}
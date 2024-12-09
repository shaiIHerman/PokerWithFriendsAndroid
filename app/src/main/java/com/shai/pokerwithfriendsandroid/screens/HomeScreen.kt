package com.shai.pokerwithfriendsandroid.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun HomeScreen(viewModel: TournamentsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    val addNewTournament: () -> Unit = {
        val newTournament = Tournament(
            id = "4",  // Unique ID for new tournament
            name = "New Tournament",
            gamesPlayed = 0
        )
        viewModel.addTournament(newTournament)
    }

    when (state) {
        is TournamentsViewState.Loading -> {
            LoadingState()
        }
        is TournamentsViewState.Success -> {
            val tournaments = (state as TournamentsViewState.Success).tournaments
            TournamentList(tournaments)
        }
        is TournamentsViewState.Error -> {
            val message = (state as TournamentsViewState.Error).message
            Text(text = "Error: $message")
        }
    }
}

@Composable
fun TournamentList(tournaments: List<Tournament>) {
    LazyColumn {
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
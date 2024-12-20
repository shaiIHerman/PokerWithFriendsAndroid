package com.shai.pokerwithfriendsandroid.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shai.pokerwithfriendsandroid.components.AppTopBar
import com.shai.pokerwithfriendsandroid.components.LoadingState
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament
import com.shai.pokerwithfriendsandroid.screens.states.TournamentsViewState
import com.shai.pokerwithfriendsandroid.ui.theme.BorderColor
import com.shai.pokerwithfriendsandroid.ui.theme.BrandColor
import com.shai.pokerwithfriendsandroid.ui.theme.Tertirary
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentsViewModel

@Composable
fun HomeScreen(
    viewModel: TournamentsViewModel = hiltViewModel(),
    onAddTournament: () -> Unit,
    onTournamentClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    // We want to use LaunchedEffect to fetch tournaments only once when the composable is first created,
    // and not from the viewmodel init because we want it to be in sync with the state controlled by
    // the viewmodel but handled by the composable.
    LaunchedEffect(key1 = Unit) { viewModel.fetchTournaments() }
    Scaffold(topBar = {
        AppTopBar(title = "Tournaments")
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = { onAddTournament() },
            containerColor = BrandColor,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Tournament")
        }
    }, containerColor = Color.Transparent
    ) { paddingValues ->
        when (state) {
            is TournamentsViewState.Loading -> {
                LoadingState()
            }

            is TournamentsViewState.Success -> {
                val tournaments = (state as TournamentsViewState.Success).tournaments
                TournamentList(paddingValues, tournaments) { onTournamentClick(it) }
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
fun TournamentList(
    paddingValues: PaddingValues, tournaments: List<Tournament>, onItemClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        items(tournaments) { tournament ->
            TournamentItem(tournament) { onItemClick(it) }
        }
    }
}

@Composable
fun TournamentItem(tournament: Tournament, onItemClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color = Color.Transparent, shape = RoundedCornerShape(8.dp))
            .border(1.dp, color = BorderColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
            .clickable { onItemClick(tournament.id) },
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
            color = Tertirary
        )
    }
}

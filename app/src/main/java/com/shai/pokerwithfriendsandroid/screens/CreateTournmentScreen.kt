package com.shai.pokerwithfriendsandroid.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.delete
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shai.pokerwithfriendsandroid.viewmodels.SearchViewModel

enum class WizardStep {
    TournamentDetails, AddPlayers
}

data class TournamentData(
    val name: String = "", val buyIn: String = ""
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateTournamentWizard(
    searchViewModel: SearchViewModel = hiltViewModel(),
    onClose: () -> Unit,
    onTournamentCreated: (TournamentData) -> Unit
) {
    var currentStep by remember { mutableStateOf(WizardStep.TournamentDetails) }
    var tournamentData by remember { mutableStateOf(TournamentData()) }

    when (currentStep) {
        WizardStep.TournamentDetails -> {
            TournamentDetailsPage(tournamentData = tournamentData,
                onTournamentDataChanged = { tournamentData = it },
                onNext = { currentStep = WizardStep.AddPlayers })
        }

        WizardStep.AddPlayers -> {
            AddPlayersPage(searchViewModel = searchViewModel,
                tournamentData = tournamentData,
                onTournamentCreated = {
                    onTournamentCreated(it)
                    onClose() // Close the wizard when the tournament is created
                })
        }
    }
}

@Composable
fun TournamentDetailsPage(
    tournamentData: TournamentData,
    onTournamentDataChanged: (TournamentData) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Create New Tournament", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = tournamentData.name,
            onValueChange = { onTournamentDataChanged(tournamentData.copy(name = it)) },
            label = { Text("Tournament Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = tournamentData.buyIn,
            onValueChange = { onTournamentDataChanged(tournamentData.copy(buyIn = it)) },
            label = { Text("Buy-In Amount") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Next")
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun AddPlayersPage(
    tournamentData: TournamentData,
    onTournamentCreated: (TournamentData) -> Unit,
    searchViewModel: SearchViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Add Players to Tournament", style = MaterialTheme.typography.headlineMedium)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(color = Color.White, shape = RoundedCornerShape(4.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search icon",
                    tint = Color.Gray
                )
                BasicTextField(
                    state = searchViewModel.searchTextFieldState, modifier = Modifier.weight(1f)
                )
            }
            AnimatedVisibility(visible = searchViewModel.searchTextFieldState.text.isNotBlank()) {
                Icon(imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete icon",
                    tint = Color.Gray,
                    modifier = Modifier.clickable {
                        searchViewModel.searchTextFieldState.edit { delete(0, length) }
                    })
            }
        }
        Button(
            onClick = { onTournamentCreated(tournamentData) }, modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Create Tournament")
        }
    }
}

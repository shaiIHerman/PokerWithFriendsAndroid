package com.shai.pokerwithfriendsandroid.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

enum class WizardStep {
    TournamentDetails, AddPlayers
}

data class TournamentData(
    val name: String = "", val buyIn: String = ""
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateTournamentWizard(
    onClose: () -> Unit, onTournamentCreated: (TournamentData) -> Unit
) {
    var backStack by remember { mutableStateOf(listOf(WizardStep.TournamentDetails)) }
    var tournamentData by remember { mutableStateOf(TournamentData()) }

    BackHandler(enabled = backStack.size > 1) {
        backStack = backStack.dropLast(1)
    }

    val currentStep = backStack.last()

    when (currentStep) {
        WizardStep.TournamentDetails -> {
            TournamentDetailsScreen(tournamentData = tournamentData,
                onTournamentDataChanged = { tournamentData = it },
                onNext = { backStack = backStack + WizardStep.AddPlayers })
        }

        WizardStep.AddPlayers -> {
            AddPlayersScreen(tournamentData = tournamentData, onTournamentCreated = {
                onTournamentCreated(it)
                onClose()
            })
        }
    }
}
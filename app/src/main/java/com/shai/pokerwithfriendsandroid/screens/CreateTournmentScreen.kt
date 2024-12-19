package com.shai.pokerwithfriendsandroid.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.shai.pokerwithfriendsandroid.viewmodels.CreateTournamentViewModel

enum class WizardStep {
    TournamentDetails, AddPlayers
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateTournamentWizard(
    createTournamentViewModel: CreateTournamentViewModel = hiltViewModel(), onClose: () -> Unit
) {
    var backStack by remember { mutableStateOf(listOf(WizardStep.TournamentDetails)) }

    BackHandler(enabled = backStack.size > 1) {
        backStack = backStack.dropLast(1)
    }

    val currentStep = backStack.last()

    when (currentStep) {
        WizardStep.TournamentDetails -> {
            TournamentDetailsScreen(viewModel = createTournamentViewModel,
                onNext = { backStack = backStack + WizardStep.AddPlayers }) {
                onClose()
            }
        }

        WizardStep.AddPlayers -> {
            AddPlayersScreen(viewModel = createTournamentViewModel, onPlayersAdded = {
                backStack = backStack.dropLast(1)
            })
        }
    }
}
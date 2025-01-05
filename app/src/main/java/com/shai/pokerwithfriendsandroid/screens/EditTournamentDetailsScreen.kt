package com.shai.pokerwithfriendsandroid.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shai.pokerwithfriendsandroid.components.AppSpacer
import com.shai.pokerwithfriendsandroid.components.HeadingTextComponent
import com.shai.pokerwithfriendsandroid.components.MyTextField
import com.shai.pokerwithfriendsandroid.components.PlayerList
import com.shai.pokerwithfriendsandroid.components.PrimaryButton
import com.shai.pokerwithfriendsandroid.components.SecondaryButton
import com.shai.pokerwithfriendsandroid.components.TextType
import com.shai.pokerwithfriendsandroid.screens.states.CreateTournamentViewState
import com.shai.pokerwithfriendsandroid.viewmodels.CreateTournamentViewModel
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentData


@Composable
fun TournamentDetailsScreen(
    viewModel: CreateTournamentViewModel = hiltViewModel(), onNext: () -> Unit, onDone: () -> Unit
) {
    val createTournamentViewState by viewModel.createTournamentUiState.observeAsState(
        CreateTournamentViewState.Idle
    )
    val context = LocalContext.current
    val tournament = viewModel.tournament.observeAsState(TournamentData())

    LaunchedEffect(createTournamentViewState) {
        when (createTournamentViewState) {
            is CreateTournamentViewState.TournamentAdded -> {
                Toast.makeText(context, "Tournament added successfully!", Toast.LENGTH_SHORT).show()
                onDone()
            }

            is CreateTournamentViewState.Error -> {
                Toast.makeText(
                    context,
                    (createTournamentViewState as CreateTournamentViewState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Non-scrollable content at the top
        HeadingTextComponent("Create New Tournament")

        AppSpacer()
        MyTextField(labelVal = "Tournament Name",
            vector = Icons.Filled.Create,
            fieldValue = tournament.value.name,
            onValueChange = { viewModel.updateTournamentName(it) })

        AppSpacer()
        MyTextField(labelVal = "Buy-In Amount",
            vector = Icons.Filled.ShoppingCart,
            fieldValue = tournament.value.buyIn,
            textType = TextType.Number,
            onValueChange = { viewModel.updateTournamentBuyIn(it) })

        AppSpacer()
        SecondaryButton("Add Players") { onNext() }

        // Scrollable PlayerList
        Box(
            modifier = Modifier
                .weight(1f) // Allow it to expand within available space
                .fillMaxWidth()
        ) {
            PlayerList(players = viewModel.tournament.value?.players) {
                viewModel.removePlayer(it)
            }
        }

        AppSpacer()

        PrimaryButton(
            "Create",
            showProgress = createTournamentViewState is CreateTournamentViewState.Adding,
            progressText = "Creating..."
        ) {
            viewModel.createTournament()
        }
    }

}

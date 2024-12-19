package com.shai.pokerwithfriendsandroid.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shai.pokerwithfriendsandroid.components.CreateUser
import com.shai.pokerwithfriendsandroid.components.DividerWithText
import com.shai.pokerwithfriendsandroid.components.HeadingTextComponent
import com.shai.pokerwithfriendsandroid.components.PrimaryButton
import com.shai.pokerwithfriendsandroid.components.SearchComponent
import com.shai.pokerwithfriendsandroid.screens.states.CreatePlayerViewState
import com.shai.pokerwithfriendsandroid.viewmodels.CreateTournamentViewModel

@ExperimentalFoundationApi
@Composable
fun AddPlayersScreen(
    viewModel: CreateTournamentViewModel = hiltViewModel(),
    onPlayersAdded: () -> Unit,
) {
    val createPlayerViewState by viewModel.createPlayerUiState.observeAsState(CreatePlayerViewState.Idle)
    var showCreate by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(createPlayerViewState) {
        when (createPlayerViewState) {
            is CreatePlayerViewState.PlayerAdded -> {
                Toast.makeText(context, "Player added successfully!", Toast.LENGTH_SHORT).show()
            }

            is CreatePlayerViewState.Error -> {
                Toast.makeText(
                    context,
                    (createPlayerViewState as CreatePlayerViewState.Error).message,
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
        HeadingTextComponent("Add Players")
        Spacer(modifier = Modifier.height(16.dp))
        SearchComponent(onEmptySearch = {
            showCreate = it
        }) { viewModel.updateTournamentPlayers(it) }
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibility(visible = showCreate) {
            Column {
                DividerWithText()
                CreateUser(createPlayerViewState = createPlayerViewState) {
                    viewModel.createPlayer(it)
                }
            }
        }
        PrimaryButton(labelVal = "Done") { onPlayersAdded() }
    }
}


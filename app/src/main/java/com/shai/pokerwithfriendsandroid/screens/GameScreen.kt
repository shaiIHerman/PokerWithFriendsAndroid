package com.shai.pokerwithfriendsandroid.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shai.pokerwithfriendsandroid.components.AppTopBar
import com.shai.pokerwithfriendsandroid.components.GameStatusCircle
import com.shai.pokerwithfriendsandroid.components.LoadingState
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame
import com.shai.pokerwithfriendsandroid.domain.models.LocalUser
import com.shai.pokerwithfriendsandroid.screens.states.GameViewState
import com.shai.pokerwithfriendsandroid.ui.theme.BrandColor
import com.shai.pokerwithfriendsandroid.ui.theme.Tertirary
import com.shai.pokerwithfriendsandroid.utils.toStringDate
import com.shai.pokerwithfriendsandroid.viewmodels.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel, onBackClicked: () -> Unit) {
    val gameDetailsViewState by viewModel.gameDetailsUiState.observeAsState(
        GameViewState.Loading
    )
    Scaffold(
        topBar = {
            AppTopBar(title = "Game Details",
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onIconClick = { onBackClicked() })
        }, containerColor = Color.Transparent
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            when (val state = gameDetailsViewState) {
                GameViewState.Loading -> LoadingState()
                is GameViewState.Success -> GameDetailsContent(state.game) {
                    viewModel.onPlayerLost(it)
                }

                is GameViewState.Error -> TODO()
            }
        }
    }
}

@Composable
fun GameDetailsContent(
    game: LocalGame,
    onRemovePlayer: (LocalUser) -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
        ) {
            GameStatusCircle(status = game.status)
            Spacer(modifier = Modifier.padding(4.dp))
            Text(text = game.status.displayName)
        }
        Text("Date Played: ${game.dateCreated.toStringDate()}")
        Text("Buy-In: ${game.buyIn}")
        Text("No. of players: ${game.players.size}")
        Spacer(modifier = Modifier.padding(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.padding(8.dp))
        Text("Game Standings:")
        LazyColumn {
            items(game.players) { player ->
                GamePlayerListItem(playerPosition = player, onRemovePlayer = {
                    onRemovePlayer(it)
                })
            }
        }
    }
}

@Composable
fun GamePlayerListItem(
    playerPosition: LocalGame.PlayerPosition, onRemovePlayer: (LocalUser) -> Unit
) {
    val (position, player) = playerPosition
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(player!!.name)
        Spacer(modifier = Modifier.weight(1f))
        if (position == 0) {
            IconButton(onClick = { onRemovePlayer(player) }) {
                Icon(Icons.Default.Clear, contentDescription = "Remove")
            }
        } else if (position == 1) {
            Text("Winner", color = BrandColor)
        } else {
            Text(position.toString(), color = Tertirary)
        }
    }
}
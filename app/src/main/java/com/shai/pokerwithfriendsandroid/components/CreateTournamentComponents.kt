package com.shai.pokerwithfriendsandroid.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shai.pokerwithfriendsandroid.domain.models.LocalUser
import com.shai.pokerwithfriendsandroid.screens.states.CreatePlayerViewState
import com.shai.pokerwithfriendsandroid.ui.theme.BrandColor
import com.shai.pokerwithfriendsandroid.ui.theme.Tertirary
import com.shai.pokerwithfriendsandroid.viewmodels.AddPlayersViewModel
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentData


@Composable
fun PlayerList(
    players: List<TournamentData.AddPlayer>?, onRemovePlayer: (TournamentData.AddPlayer) -> Unit
) {
    AppSpacer()
    Text("Selected Players:")
    if (players.isNullOrEmpty()) {
        PlayerListItem(
            player = TournamentData.AddPlayer(name = "You"),
            isYou = true,
            onRemovePlayer = {})
        return
    }
    LazyColumn {
        item {
            PlayerListItem(
                player = TournamentData.AddPlayer(name = "You"),
                isYou = true,
                onRemovePlayer = {})
        }
        items(players) { player ->
            PlayerListItem(player = player, isYou = false, onRemovePlayer = onRemovePlayer)
        }
    }
}

@Composable
fun PlayerListItem(
    player: TournamentData.AddPlayer,
    isYou: Boolean,
    onRemovePlayer: (TournamentData.AddPlayer) -> Unit
) {
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(player.name)
        Spacer(modifier = Modifier.weight(1f))
        if (!isYou) {
            IconButton(onClick = { onRemovePlayer(player) }) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Tertirary)
            }
        }
    }
}

@Composable
fun SearchComponent(
    addPlayersViewModel: AddPlayersViewModel = hiltViewModel(),
    onEmptySearch: (Boolean) -> Unit,
    onUserSelected: (TournamentData.AddPlayer) -> Unit
) {
    val screenState by addPlayersViewModel.uiState.collectAsStateWithLifecycle()
    DisposableEffect(key1 = Unit) {
        val job = addPlayersViewModel.observeUserSearch()
        onDispose { job.cancel() }
    }
    LaunchedEffect(screenState) {
        when (screenState) {
            is AddPlayersViewModel.ScreenState.Empty, is AddPlayersViewModel.ScreenState.Error -> onEmptySearch(
                true
            )

            else -> onEmptySearch(false)
        }
    }
    AnimatedVisibility(visible = screenState is AddPlayersViewModel.ScreenState.Searching) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp), color = BrandColor
        )
    }
    SearchTextField(searchTextFieldState = addPlayersViewModel.searchTextFieldState,
        onClearText = { addPlayersViewModel.onSearchCompleted() })
    Column {
        when (val state = screenState) {
            AddPlayersViewModel.ScreenState.Empty -> {}
            is AddPlayersViewModel.ScreenState.Content -> {
                if (state.results.isNotEmpty()) {
                    val users = state.results.map { Pair(true, it) }
                    ShowUsers(users = users, onIconClick = {
                        addPlayersViewModel.onSearchCompleted()
                        onUserSelected(it)
                    })
                } else {
                    AppSpacer()
                    Text("No players found", color = Color.Gray)
                }
            }

            is AddPlayersViewModel.ScreenState.Error -> {
                Log.e("SearchViewModel", "Error: ${state.message}")
            }

            AddPlayersViewModel.ScreenState.Searching -> {}
        }
    }
}

@Composable
fun ShowUsers(
    users: List<Pair<Boolean, LocalUser>>, onIconClick: (TournamentData.AddPlayer) -> Unit = {}
) {
    AppSpacer()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent, shape = RoundedCornerShape(16.dp))
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(users) { index, userPair ->
                val (isAddPlayer, user) = userPair
                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user.name,
                            modifier = Modifier.padding(16.dp), // Padding around the name text
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isAddPlayer) Tertirary else Color.White
                        )
                        IconButton(
                            onClick = { onIconClick(TournamentData.AddPlayer(user.name, user.email)) },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            if (isAddPlayer) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Item",
                                    modifier = Modifier.size(36.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Item",
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                    AppSpacer()
                    if (index < users.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 1.dp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreateUser(
    createPlayerViewState: CreatePlayerViewState,
    onUserCreated: (TournamentData.AddPlayer) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    LaunchedEffect(createPlayerViewState) {
        when (createPlayerViewState) {
            is CreatePlayerViewState.PlayerAdded -> {
                name = ""
                email = ""
            }

            else -> { /* Do nothing on idle state */
            }
        }
    }
    NameField(name, Pair(true, "")) { name = it }
    AppSpacer()
    EmailField(email = email, isValidEmail = Pair(true, ""), updateEmail = { email = it })
    //todo: validate email and name
    SecondaryButton(
        labelVal = "Create Player",
        progressText = "Adding",
        showProgress = createPlayerViewState is CreatePlayerViewState.Adding
    ) {
        val user = TournamentData.AddPlayer(name, email)
        onUserCreated(user)
    }
}
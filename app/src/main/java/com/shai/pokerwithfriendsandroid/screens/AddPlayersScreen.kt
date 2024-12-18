package com.shai.pokerwithfriendsandroid.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.delete
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shai.pokerwithfriendsandroid.components.HeadingTextComponent
import com.shai.pokerwithfriendsandroid.components.PrimaryButton
import com.shai.pokerwithfriendsandroid.components.SearchTextField
import com.shai.pokerwithfriendsandroid.db.remote.models.User
import com.shai.pokerwithfriendsandroid.ui.theme.BrandColor
import com.shai.pokerwithfriendsandroid.viewmodels.AddPlayersViewModel


@ExperimentalFoundationApi
@Composable
fun AddPlayersScreen(
    tournamentData: TournamentData,
    onTournamentCreated: (TournamentData) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HeadingTextComponent("Add Players")
        Spacer(modifier = Modifier.height(16.dp))
        SearchComponent()
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton("Create Tournament") { onTournamentCreated(tournamentData) }
    }
}

@Composable
fun SearchComponent(
    addPlayersViewModel: AddPlayersViewModel = hiltViewModel(),
) {
    DisposableEffect(key1 = Unit) {
        val job = addPlayersViewModel.observeUserSearch()
        onDispose { job.cancel() }
    }
    val screenState by addPlayersViewModel.uiState.collectAsStateWithLifecycle()

    AnimatedVisibility(visible = screenState is AddPlayersViewModel.ScreenState.Searching) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp), color = BrandColor
        )
    }
    SearchTextField(searchTextFieldState = addPlayersViewModel.searchTextFieldState,
        onClearText = { addPlayersViewModel.searchTextFieldState.edit { delete(0, length) } })
    Column {
        when (val state = screenState) {
            AddPlayersViewModel.ScreenState.Empty -> CreateUser()
            is AddPlayersViewModel.ScreenState.Content -> ShowUsers(users = state.results)
            is AddPlayersViewModel.ScreenState.Error -> {
                Log.e("SearchViewModel", "Error: ${state.message}")
            }

            AddPlayersViewModel.ScreenState.Searching -> {}
        }
    }
}

@Composable
fun ShowUsers(users: List<User>) {
    LazyColumn {
        items(users.size) { index ->
            val user = users[index]
            Text(text = user.name)
        }
    }
}

@Composable
fun CreateUser() {
    Text(text = "Create User")
}
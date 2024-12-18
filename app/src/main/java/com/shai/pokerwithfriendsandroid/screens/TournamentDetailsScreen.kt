package com.shai.pokerwithfriendsandroid.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shai.pokerwithfriendsandroid.components.HeadingTextComponent
import com.shai.pokerwithfriendsandroid.components.MyTextField
import com.shai.pokerwithfriendsandroid.components.PrimaryButton
import com.shai.pokerwithfriendsandroid.components.TextType


@Composable
fun TournamentDetailsScreen(
    tournamentData: TournamentData,
    onTournamentDataChanged: (TournamentData) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HeadingTextComponent("Create New Tournament")
        Spacer(modifier = Modifier.height(16.dp))
        MyTextField(labelVal = "Tournament Name",
            vector = Icons.Filled.Create,
            fieldValue = tournamentData.name,
            onValueChange = { onTournamentDataChanged(tournamentData.copy(name = it)) })

        Spacer(modifier = Modifier.height(16.dp))
        MyTextField(labelVal = "Buy-In Amount",
            vector = Icons.Filled.ShoppingCart,
            fieldValue = tournamentData.buyIn,
            textType = TextType.Number,
            onValueChange = { onTournamentDataChanged(tournamentData.copy(buyIn = it)) })

        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton("Next") { onNext() }
    }
}

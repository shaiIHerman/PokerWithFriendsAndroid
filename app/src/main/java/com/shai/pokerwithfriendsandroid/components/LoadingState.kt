package com.shai.pokerwithfriendsandroid.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shai.pokerwithfriendsandroid.ui.theme.Action


@Composable
fun LoadingState(modifier: Modifier = Modifier.fillMaxSize()) {
    Box(
        modifier = modifier, contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Action,
            modifier = Modifier.size(64.dp)
        )
    }
}
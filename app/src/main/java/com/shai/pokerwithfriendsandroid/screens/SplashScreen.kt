package com.shai.pokerwithfriendsandroid.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.shai.pokerwithfriendsandroid.components.LoadingState
import com.shai.pokerwithfriendsandroid.viewmodels.SplashViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(), onAuthenticated: (Boolean) -> Unit
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkAuthentication()
    }

    if (isAuthenticated == null) {
        LoadingState()
    } else {
        LaunchedEffect(isAuthenticated) {
            onAuthenticated(isAuthenticated!!)
        }
    }
}

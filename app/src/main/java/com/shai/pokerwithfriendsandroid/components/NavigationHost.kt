package com.shai.pokerwithfriendsandroid.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shai.pokerwithfriendsandroid.screens.HomeScreen
import com.shai.pokerwithfriendsandroid.screens.LoginScreen
import com.shai.pokerwithfriendsandroid.screens.SplashScreen
import com.shai.pokerwithfriendsandroid.ui.theme.Primary

@Composable
fun NavigationHost(
    navController: NavHostController, innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "splash_screen",
        modifier = Modifier
            .background(color = Primary)
            .padding(innerPadding)
    ) {
        composable(route = "splash_screen") {
            SplashScreen(onAuthenticated = { isAuthenticated ->
                if (isAuthenticated) {
                    navController.navigate("home_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                } else {
                    navController.navigate("login_screen") {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                }
            })
        }
        composable(route = "login_screen") {
            LoginScreen {
                navController.navigate("home_screen") {
                    popUpTo("login_screen") { inclusive = true }
                }
            }
        }
        composable(route = "home_screen") {
            HomeScreen()
        }
    }
}
package com.shai.pokerwithfriendsandroid.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.shai.pokerwithfriendsandroid.screens.ActiveGameScreen
import com.shai.pokerwithfriendsandroid.screens.CreateTournamentWizard
import com.shai.pokerwithfriendsandroid.screens.HomeScreen
import com.shai.pokerwithfriendsandroid.screens.LoginScreen
import com.shai.pokerwithfriendsandroid.screens.SplashScreen
import com.shai.pokerwithfriendsandroid.screens.TournamentDetailsScreen
import com.shai.pokerwithfriendsandroid.ui.theme.Primary
import com.shai.pokerwithfriendsandroid.viewmodels.ActiveGameViewModel
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentDetailsViewModel

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
            HomeScreen(onAddTournament = {
                navController.navigate("create_tournament_screen")
            }, onTournamentClick = { tournamentId ->
                navController.navigate("tournament_details/$tournamentId")
            })
        }
        composable(route = "create_tournament_screen") {
            CreateTournamentWizard(onClose = { navController.popBackStack("home_screen", false) })
        }
        composable(
            route = "tournament_details/{tournamentId}",
            arguments = listOf(navArgument("tournamentId") { type = NavType.StringType })
        ) {
            val viewmodel: TournamentDetailsViewModel = hiltViewModel()
            TournamentDetailsScreen(viewModel = viewmodel){
                navController.navigate("active_game/$it")
            }
        }
        composable(
            route = "active_game/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.StringType })
        ) {
            val viewmodel: ActiveGameViewModel = hiltViewModel()
            ActiveGameScreen(viewModel = viewmodel)
        }
    }
}
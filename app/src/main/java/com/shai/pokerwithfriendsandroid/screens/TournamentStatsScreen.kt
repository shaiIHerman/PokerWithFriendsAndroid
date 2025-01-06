package com.shai.pokerwithfriendsandroid.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shai.pokerwithfriendsandroid.components.AppTopBar
import com.shai.pokerwithfriendsandroid.components.CellText
import com.shai.pokerwithfriendsandroid.components.HeaderText
import com.shai.pokerwithfriendsandroid.components.StatsTable
import com.shai.pokerwithfriendsandroid.domain.models.LocalTournament
import com.shai.pokerwithfriendsandroid.domain.models.Statistics
import com.shai.pokerwithfriendsandroid.screens.states.TournamentDetailsViewState
import com.shai.pokerwithfriendsandroid.ui.theme.BrandColor
import com.shai.pokerwithfriendsandroid.ui.theme.Tertirary
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentDetailsViewModel

sealed class NavDestination(val title: String, val icon: ImageVector) {
    object Home : NavDestination(title = "Position", icon = Icons.Filled.Home)

    object Episodes : NavDestination(title = "Money", icon = Icons.Filled.Star)

    object Search : NavDestination(title = "Top3", icon = Icons.Filled.Place)
}

@Composable
fun TournamentStatsScreen(
    viewModel: TournamentDetailsViewModel,
    tabIndex: Int,
    onBackClicked: () -> Unit
) {
    val items = listOf(
        NavDestination.Home, NavDestination.Episodes, NavDestination.Search
    )
    var selectedIndex by remember { mutableIntStateOf(tabIndex) }

    val uiState by viewModel.tournamentDetailsUiState.collectAsState()

    Scaffold(topBar = {
        AppTopBar(title = "Tournament Stats",
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            onIconClick = { onBackClicked() })
    }, bottomBar = {
        NavigationBar(containerColor = Tertirary) {
            items.forEachIndexed { index, screen ->
                NavigationBarItem(icon = {
                    Icon(imageVector = screen.icon, contentDescription = null)
                },
                    label = { Text(screen.title) },
                    selected = index == selectedIndex,
                    onClick = { selectedIndex = index },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandColor,
                        selectedTextColor = BrandColor,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }, containerColor = Color.Transparent
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (val state = uiState) {
                is TournamentDetailsViewState.InSession, is TournamentDetailsViewState.Idle -> {
                    val tournament = (state as? TournamentDetailsViewState.Idle)?.tournament
                        ?: (state as? TournamentDetailsViewState.InSession)?.tournament
                    when (selectedIndex) {
                        0 -> ShowPositionStats(tournament!!)
                        1 -> ShowEarningsStats(tournament!!)
                        else -> ShowTop3Stats(tournament!!)
                    }
                }

                else -> Text("Error showing stats")
            }
        }
    }
}

@Composable
fun ShowPositionStats(tournament: LocalTournament) {
    val cellWidth: (Int) -> Dp = { index ->
        when (index) {
            0 -> 50.dp
            1 -> 180.dp
            else -> 100.dp
        }
    }
    val headerCellTitle: @Composable (Int) -> Unit = { index ->
        val value = when (index) {
            0 -> ""
            1 -> "Player"
            2 -> "No. of Games"
            3 -> "Played %"
            4 -> "Score"
            5 -> "Over 60 Rank"
            else -> ""
        }
        HeaderText(text = value)
    }
    val cellText: @Composable (Int, Pair<String, Any>) -> Unit = { index, item ->
        val rankingStats = item.second as LocalTournament.RankingStats
        val value = when (index) {
            0 -> rankingStats.position.toString()
            1 -> item.first
            2 -> rankingStats.stats.gamesPlayed.toString()
            3 -> "${((rankingStats.stats.gamesPlayed.toFloat() / tournament.games.size.toFloat()) * 100).toInt()}%"
            4 -> String.format("%.2f", rankingStats.stats.ranking)
            5 -> if (rankingStats.over60Position == 0) "NA" else rankingStats.over60Position.toString()
            else -> ""
        }
        CellText(text = value)
    }
    StatsTable(
        cellWidth,
        headerCellTitle,
        cellText = cellText,
        data = tournament.getStatsByRanking(),
        columnCount = 6
    )
}

@Composable
fun ShowEarningsStats(tournament: LocalTournament) {
    val cellWidth: (Int) -> Dp = { index ->
        when (index) {
            0 -> 200.dp
            else -> 100.dp
        }
    }
    val headerCellTitle: @Composable (Int) -> Unit = { index ->
        val value = when (index) {
            0 -> "Player"
            1 -> "Amount Won"
            else -> ""
        }
        HeaderText(text = value)
    }
    val cellText: @Composable (Int, Pair<String, Any>) -> Unit = { index, item ->
        val value = when (index) {
            0 -> item.first
            1 -> (item.second as Statistics).amountWon.toString()
            else -> ""
        }
        CellText(text = value)
    }
    StatsTable(
        cellWidth = cellWidth,
        headerCellTitle = headerCellTitle,
        cellText = cellText,
        tournament.getStatsByAmountWon(),
        columnCount = 2
    )
}

@Composable
fun ShowTop3Stats(tournament: LocalTournament) {
    val cellWidth: (Int) -> Dp = { index ->
        when (index) {
            0 -> 200.dp
            else -> 100.dp
        }
    }
    val headerCellTitle: @Composable (Int) -> Unit = { index ->
        val value = when (index) {
            0 -> "Player"
            1 -> "First Place"
            2 -> "Second"
            3 -> "Total"
            4 -> "Bubble"
            else -> ""
        }
        HeaderText(text = value)
    }
    val cellText: @Composable (Int, Pair<String, Any>) -> Unit = { index, item ->
        val statistics = item.second as Statistics
        val value = when (index) {
            0 -> item.first
            1 -> statistics.finalThree.firstPlace.toString()
            2 -> statistics.finalThree.secondPlace.toString()
            3 -> (statistics.finalThree.firstPlace + statistics.finalThree.secondPlace).toString()
            4 -> statistics.finalThree.bubble.toString()
            else -> ""
        }
        CellText(text = value)
    }
    StatsTable(
        cellWidth, headerCellTitle, cellText, tournament.getStatsByFinalThree(), columnCount = 5
    )
}


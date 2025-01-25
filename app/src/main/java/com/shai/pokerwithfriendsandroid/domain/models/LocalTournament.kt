package com.shai.pokerwithfriendsandroid.domain.models

import com.shai.pokerwithfriendsandroid.data.local.db.entities.TournamentEntity

data class LocalTournament(
    var id: String,
    val name: String,
    val buyIn: String,
    val playerIds: List<String>,
    val dateCreated: Long = 0,
    val dateUpdated: Long = 0,
    val adminId: String = "",
    var players: List<Pair<Boolean, LocalUser>> = emptyList(),
    var games: List<LocalGame> = emptyList(),
    var gameIds: List<String> = emptyList(),
    var statistics: HashMap<String, Statistics> = hashMapOf()
) {
    data class RankingStats(val stats: Statistics, val position: Int, val over60Position: Int)

    fun getStatsByRanking(): List<Pair<String, RankingStats>> {
        val orderedStatistics = statistics.toList().sortedBy { it.second.ranking }
        var over60Index = 0
        return orderedStatistics.mapIndexed { index, stats ->
            val player = players.find { it.second.id == stats.first }
            var over60Pos = 0
            if (stats.second.isAbove60) {
                over60Index++
                over60Pos = over60Index
            }
            Pair(
                player?.second?.name ?: "",
                RankingStats(stats = stats.second, position = index + 1, over60Position = over60Pos)
            )
        }
    }

    fun getStatsByAmountWon(): List<Pair<String, Statistics>> {
        val orderedStatistics = statistics.toList().sortedBy { it.second.amountWon }.reversed()
        return orderedStatistics.map { stats ->
            val player = players.find { it.second.id == stats.first }
            Pair(player?.second?.name ?: "", stats.second)
        }
    }

    fun getStatsByFinalThree(): List<Pair<String, Statistics>> {
        val orderedStatistics = statistics.toList().sortedWith(
            compareBy({ it.second.finalThree.firstPlace },
                { it.second.finalThree.secondPlace },
                { it.second.finalThree.bubble })
        ).reversed()
        return orderedStatistics.map { stats ->
            val player = players.find { it.second.id == stats.first }
            Pair(player?.second?.name ?: "", stats.second)
        }
    }

    fun getMoneyLeader(): String {
        val leader = statistics.maxByOrNull { it.value.amountWon }
        val player = players.find { it.second.id == leader?.key }
        return player?.second?.name ?: "No current leader"
    }

    fun getPositionLeader(): String {
        val leader = statistics.minByOrNull { it.value.ranking }
        val player = players.find { it.second.id == leader?.key }
        return player?.second?.name ?: "No current leader"
    }

    fun getFirstPlaceLeader(): String {
        val leader = statistics.maxByOrNull { it.value.finalThree.firstPlace }
        val player = players.find { it.second.id == leader?.key }
        return player?.second?.name ?: "No current leader"
    }

    fun getSecondPlaceLeader(): String {
        val leader = statistics.maxByOrNull { it.value.finalThree.secondPlace }
        val player = players.find { it.second.id == leader?.key }
        return player?.second?.name ?: "No current leader"
    }

    fun getBubbleLeader(): String {
        val leader = statistics.maxByOrNull { it.value.finalThree.bubble }
        val player = players.find { it.second.id == leader?.key }
        return player?.second?.name ?: "No current leader"
    }
}

fun LocalTournament.toTournamentEntity(): TournamentEntity {
    return TournamentEntity(
        id = id,
        name = name,
        buyIn = buyIn,
        playerIds = playerIds,
        gameIds = gameIds,
        dateCreated = dateCreated,
        dateUpdated = dateUpdated,
        adminId = adminId
    )
}

fun LocalTournament.updatePlayersAndGames(users: List<LocalUser>, games: List<LocalGame>) {
    players = users.map { localUser -> Pair(false, localUser) }
    this.games = games
    if (games.isEmpty()) return
    val playerStatistics: HashMap<String, Statistics> = hashMapOf()
    players.forEach { playerPair ->
        var ranking = 0f
        var amountWon = 0
        var gamesPlayed = 0
        val finalThree = FinalThree()

        for (game in games) {
            if(game.status == GameStatus.Active) break
            val playerInGame = game.players.find { it.player!!.id == playerPair.second.id }
            if (playerInGame != null) {
                // Player participated in the game
                ranking += playerInGame.position
                gamesPlayed++
                amountWon -= this.buyIn.toInt()

                // Update finalThree based on position
                when (playerInGame.position) {
                    1 -> {
                        amountWon += this.buyIn.toInt() * game.players.size - this.buyIn.toInt()
                        finalThree.firstPlace += 1
                    }

                    2 -> {
                        amountWon += this.buyIn.toInt()
                        finalThree.secondPlace += 1
                    }

                    3 -> {
                        finalThree.bubble += 1
                    }
                }
            }
        }

        // Adjust ranking only if the player participated in any games
        if (gamesPlayed > 0) {
            ranking /= gamesPlayed
        }
        val isAbove60 = gamesPlayed.toFloat() / games.size.toFloat() > 0.6
        val statistics = Statistics(amountWon, gamesPlayed, ranking, finalThree, isAbove60)
        playerStatistics[playerPair.second.id] = statistics
    }

    this.statistics = playerStatistics
}


data class Statistics(
    var amountWon: Int,
    var gamesPlayed: Int,
    var ranking: Float,
    var finalThree: FinalThree,
    var isAbove60: Boolean
)

data class FinalThree(var firstPlace: Int = 0, var secondPlace: Int = 0, var bubble: Int = 0)

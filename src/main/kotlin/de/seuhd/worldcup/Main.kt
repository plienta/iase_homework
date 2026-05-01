package de.seuhd.worldcup

import kotlinx.serialization.json.Json

fun main() {
    val tournament = readData()
    handleData(tournament);
    while (true) {
        printMainMenu()
        val raw = readLine()
        if (raw == null) {
            println("No input available. Try again.")
            return
        }
        when (raw.trim()) {
            "1" -> showStandings(tournament.groups)
            "2" -> showMatches(tournament.groups)
            "3" -> placeBets(tournament.groups, tournament.bets)
            "4" -> showBettingScore(tournament.groups, tournament.bets)
            "5" -> {
                println("Exiting...")
                return
            }
            else -> println("Invalid option.")
        }
    }
}

// Load JSON file from resources and deserialize
private fun readData(): WorldCupData {
    val jsonString = object {}.javaClass.getResource("/world_cup_2026_full_data.json")!!
        .readText()
    val tournament = Json.decodeFromString<WorldCupData>(jsonString)
    return tournament
}

// link team IDs to team objects and compute points/goals stats from match results
// ust run once after JSON deserialization to link team IDs to team objects
private fun handleData(tournament: WorldCupData) {
    for (group in tournament.groups) {
        group.initializeTeamObjectsInMatches()
        group.calculatePointsForAllTeamInThisGroup()
    }
}

fun printMainMenu() {
    println("===== FIFA World Cup 2026 – Betting Console =====")
    println("1) Show Standings")
    println("2) Show Matches")
    println("3) Place Bets")
    println("4) Show Betting Score")
    println("5) Exit")
    println("=================================================")
    print("Choose an option (1 to 5): ")
}

private fun getGroupOptionByName(allGroups: List<Group>, allGroupPossible: Boolean = false): List<Group> {
    if (allGroupPossible) {
        print("Which group’s do you want to take action (e.g., 'Group A') or 'All groups' ")
    } else {
        print("Which group’s do you want to take action (e.g., 'Group A')")
    }
    val input = readLine()?.trim()
    if (input.isNullOrEmpty()) {
        println("No group name entered.")
        return listOf()
    }
    var selectedGroups: List<Group>;
    if (allGroupPossible && input == "All groups") {
        selectedGroups = allGroups
    } else {
        val group = allGroups.find { it.name.equals(input, ignoreCase = true) }
        if (group == null) {
            println("Group '$input' not found.")
            return listOf()
        }
        if (group == null) {
            println("No group name entered.")
            return listOf()
        }
        selectedGroups = listOf(group)
    }
    return selectedGroups
}

/* -------------------------------------------------------------
   1) Show Standings
   ------------------------------------------------------------- */
private fun showStandings(allGroups: List<Group>) {
    // Prompt user for group name
    val selectedGroups = getGroupOptionByName(allGroups, true)
    // Sort by points desc, goal diff desc, team id asc
    for (group in selectedGroups) {
        val sorted = group.sortTeams();
        printStandingsTable(group.name, sorted);
    }
}

// Display standings table with position, team name, points, and goal stats
private fun printStandingsTable(groupName: String, sorted: List<Team>) {
    println()
    println("== $groupName ==")
    println("Pos | Team                 | Pts | GF | GA | GD")
    println("------------------------------------------------")

    sorted.forEachIndexed { index, team ->
        val name = team.name ?: "N/A"
        val pts = team.teamStat?.totalPoints?.toString() ?: "0"
        val gf = team.teamStat?.totalGoalsFor?.toString() ?: "0"
        val ga = team.teamStat?.totalGoalsAgainst?.toString() ?: "0"
        val gd = team.teamStat?.totalGoalsDiff?.toString() ?: "0"

        println(
            "${(index + 1).toString().padEnd(3)} | " +
                    "${name.padEnd(20)} | " +
                    "${pts.padEnd(3)} | " +
                    "${gf.padEnd(2)} | " +
                    "${ga.padEnd(2)} | " +
                    "${gd.padEnd(2)}"
        )
    }

    println()
}




/* -------------------------------------------------------------
   2) Show Matches
   ------------------------------------------------------------- */
// lists every match in that group, showing the date, the teams,
// and the current score (if available)
private fun showMatches(allGroups: List<Group>) {
    val groups = getGroupOptionByName(allGroups)
    if (!groups.isEmpty()) {
        val group = groups[0]
        if (group == null) {
            println("No group name entered.")
            return
        }
        printMatchesTable(group.name, group.matches);
    }

}

private fun printMatchesTable(groupName: String, matches: List<Match>) {
    println()
    println("== $groupName ==")
    println("Date         | Ground               | Home Team            | Away Team            | Home Score | Away Score | Home Points | Away Points")
    println("---------------------------------------------------------------------------------------------------------------")

    matches.forEach { match ->
        val date = match.date ?: "N/A"
        val ground = match.ground ?: "N/A"
        val homeTeam = match.homeTeamObj?.name ?: "N/A"
        val awayTeam = match.awayTeamObj?.name ?: "N/A"
        val homeScore = match.homeScore?.toString() ?: "N/A"
        val awayScore = match.awayScore?.toString() ?: "N/A"
        val homePoints = match.homeTeamObj?.teamStat?.totalPoints?.toString() ?: "N/A"
        val awayPoints = match.awayTeamObj?.teamStat?.totalPoints?.toString() ?: "N/A"

        println(
            "${date.padEnd(12)} | " +
                    "${ground.padEnd(20)} | " +
                    "${homeTeam.padEnd(20)} | " +
                    "${awayTeam.padEnd(20)} | " +
                    "${homeScore.padEnd(10)} | " +
                    "${awayScore.padEnd(10)} | " +
                    "${homePoints.padEnd(11)} | " +
                    "${awayPoints.padEnd(11)}"
        )
    }

    println()
}



/* -------------------------------------------------------------
   3) Place Bets
   ------------------------------------------------------------- */
// For every match in that group one by one.
// the user enters their tip: 1 (Home Win), 2 (Away Win), or 0 (Draw).
// these bets stored in a collection List<Bet> of tournament
private fun placeBets(allGroups: List<Group>, allBets: MutableList<Bet>) {
    val groups = getGroupOptionByName(allGroups)
    if (!groups.isEmpty()) {
        val group = groups[0]
        if (group == null) {
            println("No group name entered.")
            return
        }
        for (match in group.matches) {
            println("Match: ${match.homeTeamObj?.name} vs ${match.awayTeamObj?.name} on ${match.date}")
            print("Enter your bet (1 for Home Win, 2 for Away Win, 0 for Draw): ")
            val betInput = readLine()?.trim()
            if (betInput == "1" || betInput == "2" || betInput == "0") {
                allBets.add(
                    Bet(
                        betId = 1,
                        userId = getCurrentUserId(),
                        betValue = betInput.toInt(),
                        betMatchId = match.matchId,
                        betGroupName = group.name
                    )
                )
            } else {
                println("Skipping this match.")
            }
        }
    }
}

private fun getCurrentUserId(): Int {
    return 1; // placeholder for user ID
}

/* -------------------------------------------------------------
   4) Show Betting Score
   ------------------------------------------------------------- */
// compares the user’s stored bets against the actual results found in the JSON file.
// award 1 point for every correct outcome
// display the total score and a summary of correct vs. incorrect predictions
// if match result is not available yet, count it as unknown
private fun showBettingScore(allGroups: List<Group>, allBets: MutableList<Bet>) {
    println("Betting Score: ")
    println("Group | Match | Your Bet | Actual Result | Points Earned")
    println("---------------------------------------------")
    val userBets = allBets.filter { it.userId == getCurrentUserId() }
    var totalCorrect = 0
    var totalIncorrect = 0
    var totalUnknown = 0
    var pointsEarned = 0
    for (bet in userBets) {
        finalizeResultForBet(allGroups, bet)
        if (bet.actualResult != null) {
            if (bet.actualResult == bet.betValue) {
                totalCorrect++
                pointsEarned = 1
            } else {
                totalIncorrect++
                pointsEarned = 0
            }
        } else {
            totalUnknown++
        }
        println(
            "${bet.betGroupName.padEnd(6)} | " +
            "${bet.betMatchId.toString().padEnd(5)} | " +
            "${bet.betValue.toString().padEnd(8)} | " +
            "${bet.actualResult?.toString()?.padEnd(13) ?: "N/A".padEnd(13)} | " +
            "$pointsEarned"
        )
    }
    println()
    println("Total Correct ${totalCorrect} | Total Incorrect ${totalIncorrect} | Total Unknown ${totalUnknown} | Total Points Earned ${totalCorrect}")
}

// determine actual match outcome and store in bet.actualResult
// Returns 1 = home win, 2 = away win, 0 = draw, null if no score
private fun finalizeResultForBet(allGroups: List<Group>, bet: Bet) {
    val group = allGroups.find { it.name.equals(bet.betGroupName, ignoreCase = true) }
    if (group != null) {
        val match = group.matches.find { it.matchId == bet.betMatchId }
        if (match != null) {
            bet.actualResult = when {
                match.homeScore != null && match.awayScore != null -> {
                    when {
                        match.homeScore!! > match.awayScore!! -> 1
                        match.homeScore!! < match.awayScore!! -> 2
                        else -> 0
                    }
                }
                else -> null
            }
        }
    }
}
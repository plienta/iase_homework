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

private fun readData(): WorldCupData {
    val jsonString = object {}.javaClass.getResource("/world_cup_2026_full_data.json")!!
        .readText()
    val tournament = Json.decodeFromString<WorldCupData>(jsonString)
    return tournament
}

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

private fun getGroupOptionByName(allGroups: List<Group>): Group? {
    print("Enter group name (e.g., 'Group A'): ")
    val input = readLine()?.trim()
    if (input.isNullOrEmpty()) {
        println("No group name entered.")
        return null
    }
    val group = allGroups.find { it.name.equals(input, ignoreCase = true) }
    if (group == null) {
        println("Group '$input' not found.")
        return null
    }
    return group
}

/* -------------------------------------------------------------
   1) Show Standings
   ------------------------------------------------------------- */
// This run with the assumption that groups has been filtered
// The user should be able to view the current table of the groups.
// Position | Points | GF | GA | GD
private fun showStandings(allGroups: List<Group>) {
    val group = getGroupOptionByName(allGroups)
    if (group == null) {
        println("No group name entered.")
        return
    }
    val sorted = group.sortTeams();
    printStandingsTable(group.name, sorted);
}

private fun printStandingsTable(groupName: String, sorted: List<Team>) {
    println("== $groupName ==")
    println("Pos | Team            | Pts | GF | GA | GD")
    println("-------------------------------------------")

    sorted.forEachIndexed { index, t ->
        println(
            "${index + 1}   | " +
                    "${t.name?.padEnd(15)} | " +
                    "${t.teamStat?.totalPoints.toString().padEnd(3)} | " +
                    "${t.teamStat?.totalGoalsFor.toString().padEnd(3)} | " +
                    "${t.teamStat?.totalGoalsAgainst.toString().padEnd(3)} | " +
                    "${t.teamStat?.totalGoalsDiff}"
        )
    }
}

/* -------------------------------------------------------------
   2) Show Matches
   ------------------------------------------------------------- */
// lists every match in that group, showing the date, the teams,
// and the current score (if available)
private fun showMatches(allGroups: List<Group>) {
    val group = getGroupOptionByName(allGroups)
    if (group == null) {
        println("No group name entered.")
        return
    }
    printMatchesTable(group.name, group.matches);
}

private fun printMatchesTable(groupName: String, matches: List<Match>) {
    println("== $groupName ==")
    println("Date | Ground | Home Team | Away Team | Home Score | Away Score | Home Points | Away Points")
    println("-------------------------------------------")
    matches.forEach { match ->
        println(
            "${match.date}  | " +
            "${match.ground?.padEnd(6)} | " +
            "${match.homeTeamObj?.name?.padEnd(10)} | " +
            "${match.awayTeamObj?.name?.padEnd(10)} | " +
            "${match.homeScore?.toString()?.padEnd(10) ?: "N/A".padEnd(10)} | " +
            "${match.awayScore?.toString()?.padEnd(10) ?: "N/A".padEnd(10)} | " +
            "${match.homeTeamObj?.teamStat?.totalPoints.toString().padEnd(11) ?: "N/A".padEnd(11)} | " +
            "${match.awayTeamObj?.teamStat?.totalPoints.toString().padEnd(11) ?: "N/A".padEnd(11)}"
        );
    }
}

/* -------------------------------------------------------------
   3) Place Bets
   ------------------------------------------------------------- */
// The program iterates through every match in that group one by one.
// For each match, the user enters their tip: 1 (Home Win), 2 (Away Win), or 0 (Draw).
// These bets must be stored in a collection (e.g., a List<Bet> or Map) within the  program
private fun placeBets(allGroups: List<Group>, allBets: MutableList<Bet>) {
    val group = getGroupOptionByName(allGroups)
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
    return
}

private fun getCurrentUserId(): Int {
    return 1; // Placeholder for user ID
}

/* -------------------------------------------------------------
   4) Show Betting Score
   ------------------------------------------------------------- */
// shows how successful the user’s tips were.
// compares the user’s stored bets against the actual results found in the JSON file.
// Award 1 point for every correct outcome (Win/Loss/Draw).
// Display the total score and a summary of correct vs. incorrect predictions
private fun showBettingScore(allGroups: List<Group>, allBets: MutableList<Bet>) {
    println("Betting Score: ")
    println("Group | Match | Your Bet | Actual Result | Points Earned")
    println("---------------------------------------------")
    val userBets = allBets.filter { it.userId == getCurrentUserId() }
    for (bet in userBets) {
        finalizeResultForBet(allGroups, bet)
        val pointsEarned = if (bet.actualResult != null && bet.actualResult == bet.betValue) 1 else 0
        println(
            "${bet.betGroupName.padEnd(6)} | " +
            "${bet.betMatchId.toString().padEnd(5)} | " +
            "${bet.betValue.toString().padEnd(8)} | " +
            "${bet.actualResult?.toString()?.padEnd(13) ?: "N/A".padEnd(13)} | " +
            "$pointsEarned"
        )
    }
}

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
package de.seuhd.worldcup

import kotlinx.serialization.json.Json

fun main() {
    val tournament = readData()
    val testGroups = mutableListOf<Group>(tournament.groups[0]);
    showStandings(testGroups);
}

private fun readData(): WorldCupData {
    val jsonString = object {}.javaClass.getResource("/world_cup_2026_full_data.json")!!
        .readText()
    val tournament = Json.decodeFromString<WorldCupData>(jsonString)
    return tournament
}

/* -------------------------------------------------------------
   1) Show Standings
   ------------------------------------------------------------- */
// This run with the assumption that groups has been filtered
private fun showStandings(allGroups: List<Group>) {
    for (group in allGroups) {
        group.initializeTeamObjectsInMatches()
        group.calculatePointsForAllTeamInThisGroup();
        val sorted = group.sortTeams();
        printStandingsTable(group.name, sorted);
    }
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
private fun showMatches(allGroups: List<Group>) {
    //TODO
}

/* -------------------------------------------------------------
   3) Place Bets
   ------------------------------------------------------------- */
private fun placeBets(allGroups: List<Group>) {
    //TODO
}

/* -------------------------------------------------------------
   4) Show Betting Score
   ------------------------------------------------------------- */
private fun showBettingScore(allGroups: List<Group>) {
    //TODO
}
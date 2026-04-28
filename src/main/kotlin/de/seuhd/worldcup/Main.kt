package de.seuhd.worldcup

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

fun main() {
    val jsonString = object {}.javaClass.getResource("/world_cup_2026_full_data.json")!!
        .readText()
    val tournament = Json.decodeFromString<WorldCupData>(jsonString)
    println(tournament.groups[0].matches[0].homeTeam)

    //TODO: Implement interactive menu
}

/* -------------------------------------------------------------
   1) Show Standings
   ------------------------------------------------------------- */
private fun showStandings(allGroups: List<Group>) {
    //TODO
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
package de.seuhd.worldcup

import kotlinx.serialization.Serializable

@Serializable
data class WorldCupData(
    val tournament: String,
    val groups: List<Group>,
    val knockouts: List<Knockout>
)

@Serializable
data class Group(
    val name: String = "",
    var teams: List<Team>,
    var matches: List<Match>
)

@Serializable
data class Team(
    val id: String = "",
    val name: String? = null
)

@Serializable
data class Match(
    val matchId: Int = 0,
    val round: String? = null,
    val date: String? = null,
    val homeTeam: String? = null,
    val awayTeam: String? = null,
    var homeScore: Int? = null,
    var awayScore: Int? = null,
    val ground: String? = null,
)

@Serializable
data class Knockout(
    val matchId: Int = 0,
    val round: String? = null,
    val date: String? = null,
    val homePlaceholder: String = "",
    val awayPlaceholder: String = "",
    var homeScore: Int? = null,
    var awayScore: Int? = null,
    val ground: String? = null,
)
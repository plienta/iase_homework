package de.seuhd.worldcup

import kotlinx.serialization.Serializable

@Serializable
data class WorldCupData(
    val tournament: String,
    val groups: List<Group>,
    val knockouts: List<Knockout>,
    val bets: MutableList<Bet> = mutableListOf()
)

@Serializable
data class Group(
    val name: String = "",
    var teams: List<Team>,
    var matches: List<Match>,
) {
    fun initializeTeamObjectsInMatches() {
        for (match in matches) {
            match.initializeTeams(teams)
        }
    }

    fun sortTeams(): List<Team> {
        val sorted = teams.sortedWith(
            compareByDescending<Team> { it.teamStat?.totalPoints }
                .thenByDescending { it.teamStat?.totalGoalsDiff }
                .thenBy{it.id}
        )
        return sorted
    }

    fun calculatePointsForAllTeamInThisGroup() {
        val validMatches = matches.filter { match -> match.homeScore != null && match.awayScore != null }
        for (match in validMatches) {
            val homePointThisMatch = getPointForFirstTeam(match.homeScore!!, match.awayScore!!);
            val awayPointThisMatch = getPointForFirstTeam(match.awayScore!!, match.homeScore!!);

            match.homeTeamObj?.teamStat?.totalPoints += homePointThisMatch;
            match.homeTeamObj?.teamStat?.totalGoalsFor += match.homeScore!!;
            match.homeTeamObj?.teamStat?.totalGoalsAgainst += match.awayScore!!;
            match.homeTeamObj?.teamStat?.totalGoalsDiff += (match.homeScore!! - match.awayScore!!);

            match.awayTeamObj?.teamStat?.totalPoints += awayPointThisMatch;
            match.awayTeamObj?.teamStat?.totalGoalsFor += match.awayScore!!;
            match.awayTeamObj?.teamStat?.totalGoalsAgainst += match.homeScore!!;
            match.awayTeamObj?.teamStat?.totalGoalsDiff += (match.awayScore!! - match.homeScore!!);
        }
    }

    fun getPointForFirstTeam(firstScore: Int, secondScore: Int): Int {
        if (firstScore < secondScore) {
            return 0;
        }
        if (firstScore > secondScore) {
            return 3;
        }
        return 1;
    }
}

@Serializable
data class Team(
    val id: String = "",
    val name: String? = null,
    var teamStat: TeamStat? = TeamStat()
)

@Serializable
class TeamStat {
    var totalPoints: Int = 0
    var totalGoalsFor: Int = 0
    var totalGoalsAgainst: Int = 0
    var totalGoalsDiff: Int = 0
}

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

    // convert data from string to object
    var homeTeamObj: Team? = null,
    var awayTeamObj: Team? = null

) {
    fun initializeTeams(teams: List<Team>) {
        homeTeamObj = teams.find { t -> t.id == homeTeam }
        awayTeamObj = teams.find { t -> t.id == awayTeam }
    }
}

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

@Serializable
data class Bet (
    val betId: Int = 0,
    val userId: Int = 0,
    val betValue: Int = 0,
    val betMatchId: Int = 0,
    val betGroupName: String = "",
    var actualResult: Int? = null,
)
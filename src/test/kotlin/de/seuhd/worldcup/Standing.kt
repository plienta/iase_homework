package de.seuhd.worldcup

import kotlinx.serialization.json.Json
import kotlin.test.*

class StandingTest {

    val mockTournamentJson = "" +
            "{\n" +
            "  \"tournament\": \"World Cup 2026\",\n" +
            "  \"groups\": [\n" +
            "    {\n" +
            "      \"name\": \"Group A\",\n" +
            "      \"teams\": [\n" +
            "        {\n" +
            "          \"id\": \"CZE\",\n" +
            "          \"name\": \"Czech Republic\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"KOR\",\n" +
            "          \"name\": \"South Korea\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"MEX\",\n" +
            "          \"name\": \"Mexico\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"id\": \"RSA\",\n" +
            "          \"name\": \"South Africa\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"matches\": [\n" +
            "        {\n" +
            "          \"matchId\": 1,\n" +
            "          \"round\": \"Matchday 1\",\n" +
            "          \"date\": \"2026-06-11\",\n" +
            "          \"homeTeam\": \"MEX\",\n" +
            "          \"awayTeam\": \"RSA\",\n" +
            "          \"homeScore\": 10,\n" +
            "          \"awayScore\": 5,\n" +
            "          \"ground\": \"Mexico City\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"matchId\": 2,\n" +
            "          \"round\": \"Matchday 1\",\n" +
            "          \"date\": \"2026-06-11\",\n" +
            "          \"homeTeam\": \"KOR\",\n" +
            "          \"awayTeam\": \"CZE\",\n" +
            "          \"homeScore\": 5,\n" +
            "          \"awayScore\": 7,\n" +
            "          \"ground\": \"Guadalajara (Zapopan)\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"matchId\": 3,\n" +
            "          \"round\": \"Matchday 8\",\n" +
            "          \"date\": \"2026-06-18\",\n" +
            "          \"homeTeam\": \"CZE\",\n" +
            "          \"awayTeam\": \"RSA\",\n" +
            "          \"homeScore\": 8,\n" +
            "          \"awayScore\": 6,\n" +
            "          \"ground\": \"Atlanta\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"matchId\": 4,\n" +
            "          \"round\": \"Matchday 8\",\n" +
            "          \"date\": \"2026-06-18\",\n" +
            "          \"homeTeam\": \"MEX\",\n" +
            "          \"awayTeam\": \"KOR\",\n" +
            "          \"homeScore\": 8,\n" +
            "          \"awayScore\": 5,\n" +
            "          \"ground\": \"Guadalajara (Zapopan)\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"matchId\": 5,\n" +
            "          \"round\": \"Matchday 14\",\n" +
            "          \"date\": \"2026-06-24\",\n" +
            "          \"homeTeam\": \"CZE\",\n" +
            "          \"awayTeam\": \"MEX\",\n" +
            "          \"homeScore\": 4,\n" +
            "          \"awayScore\": 4,\n" +
            "          \"ground\": \"Mexico City\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"matchId\": 6,\n" +
            "          \"round\": \"Matchday 14\",\n" +
            "          \"date\": \"2026-06-24\",\n" +
            "          \"homeTeam\": \"RSA\",\n" +
            "          \"awayTeam\": \"KOR\",\n" +
            "          \"homeScore\": 6,\n" +
            "          \"awayScore\": 6,\n" +
            "          \"ground\": \"Monterrey (Guadalupe)\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }]," +
            "\"knockouts\": []" +
            "}"
    val tournament = Json.decodeFromString<WorldCupData>(mockTournamentJson)

    @BeforeTest
    fun setup() {
        for (group in tournament.groups) {
            group.initializeTeamObjectsInMatches()
            group.calculatePointsForAllTeamInThisGroup()
        }
    }

    @Test
    fun testStandingsLogicWithMockData() {
        val group = tournament.groups.first()

        val mex = group.teams.find { it.id == "MEX" }!!
        val cze = group.teams.find { it.id == "CZE" }!!
        val kor = group.teams.find { it.id == "KOR" }!!
        val rsa = group.teams.find { it.id == "RSA" }!!

        // Points
        assertEquals(7, mex.teamStat.totalPoints)
        assertEquals(7, cze.teamStat.totalPoints)
        assertEquals(1, kor.teamStat.totalPoints)
        assertEquals(1, rsa.teamStat.totalPoints)

        // Goal Difference
        assertEquals(8, mex.teamStat.totalGoalsDiff)
        assertEquals(4, cze.teamStat.totalGoalsDiff)
    }
}
package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SupabaseTournamentRow(
    val id: String,
    val content: TournamentContent? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class TournamentContent(
    val settings: TournamentSettings = TournamentSettings(),
    val teams: List<Team> = emptyList(),
    val fixtures: List<Fixture> = emptyList(),
    val standings: List<Standing> = emptyList(),
    val champions: List<Champion> = emptyList(),
    val contacts: List<ContactPerson> = emptyList(),
    val dignitaries: List<Dignitary> = emptyList(),
    val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class TournamentSettings(
    val name: String = "Gulabi Devi Memorial Cup",
    val edition: String = "14th Edition",
    val year: String = "2026",
    val organizer: String = "Budge Budge Institute of Technology",
    val date: String = "25-28 Sep 2026",
    val status: String = "Registration open",
    val registrationOpen: Boolean = true,
    val entryFee: Int = 3000,
    val format: String = "League-cum-Knockout",
    val matchType: String = "11-a-side Football",
    val venue: String = "BBIT Football Ground",
    val address: String = "Budge Budge Institute of Technology, Nischintapur, Budge Budge, Kolkata, West Bengal 700137",
    val mapLink: String? = "https://maps.google.com/?q=Budge+Budge+Institute+of+Technology",
    val about: String = "The 14th edition of Gulabi Devi Memorial Cup brings together top collegiate football teams across West Bengal for an intense 4-day festival of skill, passion, and sportsmanship.",
    val registrationNote: String? = "Contact the tournament office to receive official registration instructions."
)

@JsonClass(generateAdapter = true)
data class Team(
    val id: String = "",
    val name: String = "",
    val shortName: String? = null,
    val city: String? = null,
    val group: String? = null,
    val logo: String? = null
) {
    val displayShortName: String
        get() = shortName?.takeIf { it.isNotBlank() }
            ?: name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(4).joinToString("")
}

@JsonClass(generateAdapter = true)
data class Fixture(
    val id: String = "",
    val home: String = "",
    val away: String = "",
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val date: String? = null,
    val time: String? = null,
    val stage: String? = null,
    val venue: String? = null,
    val status: String? = null, // "scheduled", "live", "completed"
    val minute: String? = null, // e.g. "64'", "HT", "FT"
    val liveNote: String? = null,
    val events: List<MatchEvent>? = emptyList()
) {
    val isLive: Boolean
        get() = status?.equals("live", ignoreCase = true) == true

    val isCompleted: Boolean
        get() = status?.equals("completed", ignoreCase = true) == true

    val isScheduled: Boolean
        get() = !isLive && !isCompleted
}

@JsonClass(generateAdapter = true)
data class MatchEvent(
    val id: String = "",
    val type: String = "goal", // "goal", "yellow_card", "red_card"
    val player: String? = null,
    val minute: String? = null,
    val team: String? = null // "home" or "away"
)

@JsonClass(generateAdapter = true)
data class Standing(
    val team: String = "",
    val played: Int? = 0,
    val wins: Int? = 0,
    val draws: Int? = 0,
    val losses: Int? = 0,
    val gf: Int? = 0,
    val ga: Int? = 0,
    val points: Int? = null,
    val pts: Int? = null
) {
    val totalPoints: Int
        get() = points ?: pts ?: ((wins ?: 0) * 3 + (draws ?: 0))

    val goalDifference: Int
        get() = (gf ?: 0) - (ga ?: 0)
}

@JsonClass(generateAdapter = true)
data class Champion(
    val id: String = "",
    val year: String = "",
    val winner: String = "",
    val runnerUp: String = ""
)

@JsonClass(generateAdapter = true)
data class ContactPerson(
    val id: String = "",
    val name: String = "",
    val role: String = "",
    val phone: String = ""
)

@JsonClass(generateAdapter = true)
data class Dignitary(
    val id: String = "",
    val title: String? = null,
    val name: String = "",
    val role: String = "",
    val image: String? = null
)

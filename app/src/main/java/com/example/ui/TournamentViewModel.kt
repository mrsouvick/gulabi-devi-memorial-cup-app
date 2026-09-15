package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.SupabaseApiService
import com.example.data.local.TournamentDatabase
import com.example.data.model.Fixture
import com.example.data.model.Standing
import com.example.data.model.Team
import com.example.data.repository.TournamentRepository
import com.example.data.repository.TournamentUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TournamentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TournamentRepository

    val uiState: StateFlow<TournamentUiState>

    private val _selectedFixtureId = MutableStateFlow<String?>(null)
    val selectedFixtureId: StateFlow<String?> = _selectedFixtureId.asStateFlow()

    private val _selectedTeamId = MutableStateFlow<String?>(null)
    val selectedTeamId: StateFlow<String?> = _selectedTeamId.asStateFlow()

    init {
        val database = TournamentDatabase.getInstance(application)
        val apiService = SupabaseApiService.create()
        repository = TournamentRepository(apiService, database.tournamentDao(), viewModelScope)
        uiState = repository.uiState
    }

    fun refresh() {
        viewModelScope.launch {
            repository.fetchFromRemote()
            repository.reconnectRealtime()
        }
    }

    fun selectFixture(fixtureId: String?) {
        _selectedFixtureId.value = fixtureId
    }

    fun selectTeam(teamId: String?) {
        _selectedTeamId.value = teamId
    }

    val liveMatches: StateFlow<List<Fixture>> = uiState.map { state ->
        state.content.fixtures.filter { it.isLive }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingMatches: StateFlow<List<Fixture>> = uiState.map { state ->
        state.content.fixtures.filter { it.isScheduled }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedMatches: StateFlow<List<Fixture>> = uiState.map { state ->
        state.content.fixtures.filter { it.isCompleted }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val computedStandings: StateFlow<List<Standing>> = uiState.map { state ->
        val explicitStandings = state.content.standings
        if (explicitStandings.isNotEmpty()) {
            explicitStandings.sortedWith(
                compareByDescending<Standing> { it.totalPoints }
                    .thenByDescending { it.goalDifference }
                    .thenByDescending { it.gf ?: 0 }
            )
        } else {
            // Compute from completed fixtures if admin hasn't explicitly entered standings table yet
            calculateStandingsFromFixtures(state.content.teams, state.content.fixtures)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    override fun onCleared() {
        super.onCleared()
        repository.disconnectRealtime()
    }

    private fun calculateStandingsFromFixtures(teams: List<Team>, fixtures: List<Fixture>): List<Standing> {
        val map = mutableMapOf<String, StandingData>()

        teams.forEach { team ->
            map[team.name] = StandingData(team = team.name)
        }

        fixtures.filter { it.isCompleted }.forEach { f ->
            val home = f.home
            val away = f.away
            val hScore = f.homeScore ?: 0
            val aScore = f.awayScore ?: 0

            val homeData = map.getOrPut(home) { StandingData(team = home) }
            val awayData = map.getOrPut(away) { StandingData(team = away) }

            homeData.played++
            awayData.played++
            homeData.gf += hScore
            homeData.ga += aScore
            awayData.gf += aScore
            awayData.ga += hScore

            when {
                hScore > aScore -> {
                    homeData.wins++
                    homeData.points += 3
                    awayData.losses++
                }
                aScore > hScore -> {
                    awayData.wins++
                    awayData.points += 3
                    homeData.losses++
                }
                else -> {
                    homeData.draws++
                    awayData.draws++
                    homeData.points += 1
                    awayData.points += 1
                }
            }
        }

        return map.values.map {
            Standing(
                team = it.team,
                played = it.played,
                wins = it.wins,
                draws = it.draws,
                losses = it.losses,
                gf = it.gf,
                ga = it.ga,
                points = it.points
            )
        }.sortedWith(
            compareByDescending<Standing> { it.totalPoints }
                .thenByDescending { it.goalDifference }
                .thenByDescending { it.gf ?: 0 }
        )
    }

    private class StandingData(
        val team: String,
        var played: Int = 0,
        var wins: Int = 0,
        var draws: Int = 0,
        var losses: Int = 0,
        var gf: Int = 0,
        var ga: Int = 0,
        var points: Int = 0
    )
}

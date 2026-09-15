package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.bracket.BracketScreen
import com.example.ui.fixtures.FixturesScreen
import com.example.ui.home.HomeScreen
import com.example.ui.match.MatchDetailScreen
import com.example.ui.more.MoreScreen
import com.example.ui.standings.StandingsScreen
import com.example.ui.teams.TeamDetailScreen
import com.example.ui.teams.TeamsScreen
import com.example.ui.theme.ChampionshipGold
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PitchDarkEmerald
import com.example.ui.theme.StadiumBorder
import com.example.ui.theme.StadiumSurface
import com.example.ui.theme.StadiumSurfaceElevated
import com.example.ui.theme.TextCream
import com.example.ui.theme.TextMuted

enum class TournamentTab(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Default.Home, "tab_home"),
    FIXTURES("Fixtures", Icons.Default.CalendarMonth, "tab_fixtures"),
    STANDINGS("Table", Icons.Default.Leaderboard, "tab_standings"),
    TEAMS("Teams", Icons.Default.Group, "tab_teams"),
    BRACKET("Bracket", Icons.Default.AccountTree, "tab_bracket"),
    MORE("More", Icons.Default.MoreHoriz, "tab_more")
}

@Composable
fun TournamentApp(
    viewModel: TournamentViewModel = viewModel()
) {
    var currentTab by remember { mutableStateOf(TournamentTab.HOME) }
    val selectedFixtureId by viewModel.selectedFixtureId.collectAsState()
    val selectedTeamId by viewModel.selectedTeamId.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PitchBlack,
        bottomBar = {
            // Only show bottom navigation when not in full-screen detail modals
            if (selectedFixtureId == null && selectedTeamId == null) {
                NavigationBar(
                    containerColor = StadiumSurface,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                        .border(
                            1.dp,
                            StadiumBorder,
                            RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                        )
                        .testTag("tournament_bottom_nav")
                ) {
                    TournamentTab.values().forEach { tab ->
                        val selected = tab == currentTab
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PitchBlack,
                                selectedTextColor = ElectricLime,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = ElectricLime
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Tab Content
            when (currentTab) {
                TournamentTab.HOME -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToFixtures = { currentTab = TournamentTab.FIXTURES },
                        onNavigateToStandings = { currentTab = TournamentTab.STANDINGS },
                        onNavigateToTeams = { currentTab = TournamentTab.TEAMS },
                        onNavigateToBracket = { currentTab = TournamentTab.BRACKET },
                        onSelectFixture = { id -> viewModel.selectFixture(id) }
                    )
                }
                TournamentTab.FIXTURES -> {
                    FixturesScreen(
                        viewModel = viewModel,
                        onSelectFixture = { id -> viewModel.selectFixture(id) }
                    )
                }
                TournamentTab.STANDINGS -> {
                    StandingsScreen(viewModel = viewModel)
                }
                TournamentTab.TEAMS -> {
                    TeamsScreen(
                        viewModel = viewModel,
                        onSelectTeam = { id -> viewModel.selectTeam(id) }
                    )
                }
                TournamentTab.BRACKET -> {
                    BracketScreen(
                        viewModel = viewModel,
                        onSelectFixture = { id -> viewModel.selectFixture(id) }
                    )
                }
                TournamentTab.MORE -> {
                    MoreScreen(viewModel = viewModel)
                }
            }

            // Detail Screen Overlays with Smooth Animation
            AnimatedVisibility(
                visible = selectedFixtureId != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                selectedFixtureId?.let { fixtureId ->
                    MatchDetailScreen(
                        fixtureId = fixtureId,
                        viewModel = viewModel,
                        onBack = { viewModel.selectFixture(null) }
                    )
                }
            }

            AnimatedVisibility(
                visible = selectedTeamId != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                selectedTeamId?.let { teamId ->
                    TeamDetailScreen(
                        teamId = teamId,
                        viewModel = viewModel,
                        onBack = { viewModel.selectTeam(null) },
                        onSelectFixture = { fixtureId ->
                            viewModel.selectTeam(null)
                            viewModel.selectFixture(fixtureId)
                        }
                    )
                }
            }
        }
    }
}

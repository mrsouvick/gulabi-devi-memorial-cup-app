package com.example.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Fixture
import com.example.ui.TournamentViewModel
import com.example.ui.components.EmptyTournamentState
import com.example.ui.components.LivePulseDot
import com.example.ui.components.MatchCard
import com.example.ui.components.OfflineErrorBanner
import com.example.ui.theme.ChampionshipGold
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.ElectricLimeSoft
import com.example.ui.theme.MatchLiveCoral
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PitchDarkEmerald
import com.example.ui.theme.StadiumBorder
import com.example.ui.theme.StadiumForest
import com.example.ui.theme.StadiumSurface
import com.example.ui.theme.StadiumSurfaceElevated
import com.example.ui.theme.TextCream
import com.example.ui.theme.TextMuted

@Composable
fun HomeScreen(
    viewModel: TournamentViewModel,
    onNavigateToFixtures: () -> Unit,
    onNavigateToStandings: () -> Unit,
    onNavigateToTeams: () -> Unit,
    onNavigateToBracket: () -> Unit,
    onSelectFixture: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val liveMatches by viewModel.liveMatches.collectAsState()
    val upcomingMatches by viewModel.upcomingMatches.collectAsState()
    val completedMatches by viewModel.completedMatches.collectAsState()

    val liveMatch = liveMatches.firstOrNull()
    val nextMatch = upcomingMatches.firstOrNull()
    val lastResult = completedMatches.firstOrNull()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PitchBlack,
                        PitchDarkEmerald,
                        PitchBlack
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // 1. Top Official Header
            item {
                TournamentHeaderSection(
                    edition = uiState.content.settings.edition,
                    organizer = uiState.content.settings.organizer,
                    onRefresh = { viewModel.refresh() }
                )
            }

            // Offline warning banner if needed
            if (uiState.isOffline) {
                item {
                    OfflineErrorBanner(onRetry = { viewModel.refresh() })
                }
            }

            // Loading state on initial fetch
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = ElectricLime)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Loading tournament data...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // 2. LIVE MATCH Section
            item {
                SectionHeader(
                    title = "LIVE MATCH",
                    subtitle = if (liveMatch != null) "Real-time score & pitch updates" else null,
                    isLive = liveMatch != null
                )
            }

            item {
                if (liveMatch != null) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        MatchCard(
                            fixture = liveMatch,
                            onClick = { onSelectFixture(liveMatch.id) }
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(36.dp),
                                shape = CircleShape,
                                color = StadiumSurfaceElevated
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "No live match right now",
                                    color = TextCream,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Scores will stream automatically pitch-side when live",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // 3. NEXT MATCH Section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                SectionHeader(title = "NEXT MATCH")
            }

            item {
                if (nextMatch != null) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        MatchCard(
                            fixture = nextMatch,
                            onClick = { onSelectFixture(nextMatch.id) }
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                    ) {
                        Text(
                            text = "No upcoming matches scheduled at this time",
                            color = TextMuted,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // 4. LAST RESULT Section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                SectionHeader(title = "LAST RESULT")
            }

            item {
                if (lastResult != null) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        MatchCard(
                            fixture = lastResult,
                            onClick = { onSelectFixture(lastResult.id) }
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                    ) {
                        Text(
                            text = "No completed matches yet",
                            color = TextMuted,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // 5. QUICK ACTIONS Grid
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "TOURNAMENT HUBS")
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        icon = Icons.Default.CalendarMonth,
                        label = "Fixtures",
                        count = "${uiState.content.fixtures.size} Matches",
                        accentColor = ElectricLime,
                        onClick = onNavigateToFixtures,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        icon = Icons.Default.Leaderboard,
                        label = "Standings",
                        count = "League Table",
                        accentColor = ChampionshipGold,
                        onClick = onNavigateToStandings,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        icon = Icons.Default.Group,
                        label = "Teams",
                        count = "${uiState.content.teams.size} Colleges",
                        accentColor = ElectricLimeSoft,
                        onClick = onNavigateToTeams,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        icon = Icons.Default.AccountTree,
                        label = "Bracket",
                        count = "Knockout Path",
                        accentColor = ChampionshipGold,
                        onClick = onNavigateToBracket,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TournamentHeaderSection(
    edition: String,
    organizer: String,
    onRefresh: () -> Unit
) {
    Surface(
        color = StadiumForest.copy(alpha = 0.35f),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
        ) {
            // Status bar top row: manual refresh action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.testTag("refresh_icon_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Data",
                        tint = ElectricLime
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Brand Identity: Official Logo + Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Official tournament logo
                Image(
                    painter = painterResource(id = R.drawable.tournament_logo),
                    contentDescription = "Gulabi Devi Memorial Cup Logo",
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.5.dp, ChampionshipGold, RoundedCornerShape(14.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Surface(
                        color = ChampionshipGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = edition.uppercase(),
                            color = ChampionshipGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "GULABI DEVI",
                        color = TextCream,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "MEMORIAL CUP",
                        color = ElectricLime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = organizer,
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String? = null,
    isLive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isLive) {
                LivePulseDot()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = title,
                color = if (isLive) MatchLiveCoral else TextCream,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
        if (subtitle != null) {
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun QuickActionTile(
    icon: ImageVector,
    label: String,
    count: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("quick_action_${label.lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StadiumSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = StadiumSurfaceElevated
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label,
                color = TextCream,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = count,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

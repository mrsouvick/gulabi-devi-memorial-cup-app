package com.example.ui.match

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Fixture
import com.example.data.model.MatchEvent
import com.example.ui.TournamentViewModel
import com.example.ui.components.EmptyTournamentState
import com.example.ui.components.LivePulseDot
import com.example.ui.components.MatchStatusBadge
import com.example.ui.components.TeamCrestBadge
import com.example.ui.theme.ChampionshipGold
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.MatchLiveCoral
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PitchDarkEmerald
import com.example.ui.theme.RedCardColor
import com.example.ui.theme.StadiumBorder
import com.example.ui.theme.StadiumForest
import com.example.ui.theme.StadiumSurface
import com.example.ui.theme.StadiumSurfaceElevated
import com.example.ui.theme.TextCream
import com.example.ui.theme.TextMuted
import com.example.ui.theme.YellowCardColor

@Composable
fun MatchDetailScreen(
    fixtureId: String,
    viewModel: TournamentViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val fixture = uiState.content.fixtures.find { it.id == fixtureId }

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
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("match_detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ElectricLime
                    )
                }

                Text(
                    text = "MATCH CENTRE",
                    color = TextCream,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            if (fixture == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Match not found", color = TextMuted)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp)
                ) {
                    // Big Score Card
                    item {
                        BigScoreCard(fixture = fixture)
                    }

                    // Live Commentary / Note if available
                    if (!fixture.liveNote.isNullOrBlank()) {
                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (fixture.isLive) MatchLiveCoral.copy(alpha = 0.15f) else StadiumSurface
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (fixture.isLive) MatchLiveCoral.copy(alpha = 0.5f) else StadiumBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    if (fixture.isLive) {
                                        LivePulseDot(modifier = Modifier.padding(top = 4.dp))
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.SportsSoccer,
                                            contentDescription = null,
                                            tint = ElectricLime,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (fixture.isLive) "PITCH-SIDE COMMENTARY" else "MATCH NOTE",
                                            color = if (fixture.isLive) MatchLiveCoral else ChampionshipGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            letterSpacing = 0.5.sp
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = fixture.liveNote,
                                            color = TextCream,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Venue and Schedule info
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                            border = BorderStroke(1.dp, StadiumBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = ElectricLime,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = "VENUE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text(text = fixture.venue ?: "BBIT Football Ground", color = TextCream, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = ChampionshipGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = "DATE & KICKOFF", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "${fixture.date ?: "TBD"} at ${fixture.time ?: "TBD"}",
                                            color = TextCream,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Match Events Timeline
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "MATCH TIMELINE & KEY EVENTS",
                            color = ElectricLime,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    val events = fixture.events.orEmpty()
                    if (events.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                                border = BorderStroke(1.dp, StadiumBorder)
                            ) {
                                EmptyTournamentState(
                                    title = "No match events recorded",
                                    message = if (fixture.isLive) "Events such as goals, cards, and substitutions will stream here in real time as they occur."
                                    else "Goal scorers and cards have not been logged for this match."
                                )
                            }
                        }
                    } else {
                        items(events) { event ->
                            MatchEventItem(event = event, homeTeam = fixture.home, awayTeam = fixture.away)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BigScoreCard(fixture: Fixture) {
    val isLive = fixture.isLive
    val backgroundBrush = if (isLive) {
        Brush.verticalGradient(
            colors = listOf(
                MatchLiveCoral.copy(alpha = 0.15f),
                StadiumSurfaceElevated
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                StadiumSurfaceElevated,
                StadiumSurface
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("big_score_card"),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            1.5.dp,
            if (isLive) MatchLiveCoral else StadiumBorder
        ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.background(backgroundBrush)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Stage & Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = StadiumForest,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = fixture.stage?.uppercase() ?: "TOURNAMENT MATCH",
                            color = ChampionshipGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    MatchStatusBadge(status = fixture.status, minute = fixture.minute)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Teams and Huge Score
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Home
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TeamCrestBadge(teamName = fixture.home, size = 64)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = fixture.home,
                            color = TextCream,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Score
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (fixture.isLive || fixture.isCompleted) {
                            Text(
                                text = "${fixture.homeScore ?: 0} - ${fixture.awayScore ?: 0}",
                                color = if (isLive) MatchLiveCoral else ElectricLime,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                            if (!fixture.minute.isNullOrBlank()) {
                                Text(
                                    text = fixture.minute,
                                    color = if (isLive) MatchLiveCoral else TextMuted,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Surface(
                                color = StadiumForest,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "VS",
                                    color = ChampionshipGold,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Away
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TeamCrestBadge(teamName = fixture.away, size = 64)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = fixture.away,
                            color = TextCream,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchEventItem(
    event: MatchEvent,
    homeTeam: String,
    awayTeam: String
) {
    val isHome = event.team.equals("home", ignoreCase = true) ||
            event.team.equals(homeTeam, ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = StadiumSurface),
        border = BorderStroke(1.dp, StadiumBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Event Icon
                when (event.type.lowercase()) {
                    "goal" -> {
                        Icon(
                            imageVector = Icons.Default.SportsSoccer,
                            contentDescription = "Goal",
                            tint = ElectricLime,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    "yellow_card" -> {
                        Box(
                            modifier = Modifier
                                .size(width = 14.dp, height = 18.dp)
                                .background(YellowCardColor, RoundedCornerShape(2.dp))
                        )
                    }
                    "red_card" -> {
                        Box(
                            modifier = Modifier
                                .size(width = 14.dp, height = 18.dp)
                                .background(RedCardColor, RoundedCornerShape(2.dp))
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.FiberManualRecord,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = event.player ?: "Player",
                        color = TextCream,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (isHome) homeTeam else awayTeam,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            // Minute badge
            Surface(
                color = StadiumForest,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = if (event.minute != null) "${event.minute}'" else "--",
                    color = ChampionshipGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

package com.example.ui.teams

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TournamentViewModel
import com.example.ui.components.MatchCard
import com.example.ui.components.TeamCrestBadge
import com.example.ui.theme.ChampionshipGold
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PitchDarkEmerald
import com.example.ui.theme.StadiumBorder
import com.example.ui.theme.StadiumForest
import com.example.ui.theme.StadiumSurface
import com.example.ui.theme.StadiumSurfaceElevated
import com.example.ui.theme.TextCream
import com.example.ui.theme.TextMuted

@Composable
fun TeamDetailScreen(
    teamId: String,
    viewModel: TournamentViewModel,
    onBack: () -> Unit,
    onSelectFixture: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val standings by viewModel.computedStandings.collectAsState()

    val team = uiState.content.teams.find { it.id == teamId || it.name == teamId }
        ?: uiState.content.teams.firstOrNull()

    val teamStanding = standings.find { it.team.equals(team?.name, ignoreCase = true) }

    val teamFixtures = remember(uiState.content.fixtures, team?.name) {
        if (team == null) emptyList()
        else uiState.content.fixtures.filter {
            it.home.equals(team.name, ignoreCase = true) || it.away.equals(team.name, ignoreCase = true)
        }
    }

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
                    modifier = Modifier.testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ElectricLime
                    )
                }

                Text(
                    text = "TEAM PROFILE",
                    color = TextCream,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            if (team == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Team not found", color = TextMuted)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp)
                ) {
                    // Team Banner
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, StadiumBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TeamCrestBadge(
                                    teamName = team.name,
                                    shortCode = team.shortName,
                                    size = 72
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = team.name,
                                    color = TextCream,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                )

                                if (!team.city.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocationCity,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = team.city,
                                            color = TextMuted,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                if (!team.group.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        color = StadiumForest,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = team.group,
                                            color = ChampionshipGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Stats Grid
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "TOURNAMENT PERFORMANCE",
                            color = ElectricLime,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatBox("Played", "${teamStanding?.played ?: 0}", Modifier.weight(1f))
                            StatBox("Wins", "${teamStanding?.wins ?: 0}", Modifier.weight(1f))
                            StatBox("Draws", "${teamStanding?.draws ?: 0}", Modifier.weight(1f))
                            StatBox("Losses", "${teamStanding?.losses ?: 0}", Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatBox("Goals (GF)", "${teamStanding?.gf ?: 0}", Modifier.weight(1f))
                            StatBox("Conceded (GA)", "${teamStanding?.ga ?: 0}", Modifier.weight(1f))
                            val diff = teamStanding?.goalDifference ?: 0
                            StatBox("GD", if (diff > 0) "+$diff" else "$diff", Modifier.weight(1f))
                            StatBox("Points", "${teamStanding?.totalPoints ?: 0}", Modifier.weight(1f), isHighlight = true)
                        }
                    }

                    // Team Fixtures
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "TEAM FIXTURES & RESULTS",
                            color = ElectricLime,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (teamFixtures.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = StadiumSurface)
                            ) {
                                Text(
                                    text = "No matches scheduled for this team yet.",
                                    color = TextMuted,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        items(teamFixtures) { fixture ->
                            MatchCard(
                                fixture = fixture,
                                onClick = { onSelectFixture(fixture.id) },
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    isHighlight: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlight) StadiumSurfaceElevated else StadiumSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isHighlight) ElectricLime.copy(alpha = 0.5f) else StadiumBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = if (isHighlight) ElectricLime else TextCream,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = TextMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

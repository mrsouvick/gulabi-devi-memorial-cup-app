package com.example.ui.bracket

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Fixture
import com.example.ui.TournamentViewModel
import com.example.ui.components.EmptyTournamentState
import com.example.ui.components.MatchStatusBadge
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
fun BracketScreen(
    viewModel: TournamentViewModel,
    onSelectFixture: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val allFixtures = uiState.content.fixtures

    val quarterFinals = remember(allFixtures) {
        allFixtures.filter { it.stage?.contains("quarter", ignoreCase = true) == true }
    }
    val semiFinals = remember(allFixtures) {
        allFixtures.filter { it.stage?.contains("semi", ignoreCase = true) == true }
    }
    val grandFinals = remember(allFixtures) {
        allFixtures.filter {
            it.stage?.contains("final", ignoreCase = true) == true &&
                    it.stage?.contains("semi", ignoreCase = true) != true &&
                    it.stage?.contains("quarter", ignoreCase = true) != true
        }
    }

    val hasKnockouts = quarterFinals.isNotEmpty() || semiFinals.isNotEmpty() || grandFinals.isNotEmpty()

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
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ROAD TO GLORY",
                        color = ChampionshipGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Knockout Bracket",
                        color = TextCream,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                IconButton(
                    onClick = { viewModel.refresh() },
                    modifier = Modifier.testTag("refresh_bracket_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = ElectricLime
                    )
                }
            }

            if (!hasKnockouts) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyTournamentState(
                        title = "Knockout Stage Pending",
                        message = "The bracket will populate as the group stage concludes and teams advance to Semi-Finals and the Grand Final."
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Grand Final Hero Section
                    if (grandFinals.isNotEmpty()) {
                        item {
                            KnockoutStageHeader(
                                stageName = "CHAMPIONSHIP GRAND FINAL",
                                isFinal = true
                            )
                        }
                        items(grandFinals) { finalMatch ->
                            BracketMatchCard(
                                fixture = finalMatch,
                                isGrandFinal = true,
                                onClick = { onSelectFixture(finalMatch.id) }
                            )
                        }
                    }

                    // Semi-Finals
                    if (semiFinals.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            KnockoutStageHeader(stageName = "SEMI-FINALS")
                        }
                        items(semiFinals) { semiMatch ->
                            BracketMatchCard(
                                fixture = semiMatch,
                                isGrandFinal = false,
                                onClick = { onSelectFixture(semiMatch.id) }
                            )
                        }
                    }

                    // Quarter-Finals
                    if (quarterFinals.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            KnockoutStageHeader(stageName = "QUARTER-FINALS")
                        }
                        items(quarterFinals) { qfMatch ->
                            BracketMatchCard(
                                fixture = qfMatch,
                                isGrandFinal = false,
                                onClick = { onSelectFixture(qfMatch.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KnockoutStageHeader(stageName: String, isFinal: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isFinal) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = ChampionshipGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = stageName,
            color = if (isFinal) ChampionshipGold else ElectricLime,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun BracketMatchCard(
    fixture: Fixture,
    isGrandFinal: Boolean,
    onClick: () -> Unit
) {
    val homeWon = fixture.isCompleted && (fixture.homeScore ?: 0) > (fixture.awayScore ?: 0)
    val awayWon = fixture.isCompleted && (fixture.awayScore ?: 0) > (fixture.homeScore ?: 0)

    val borderStroke = if (isGrandFinal) {
        BorderStroke(1.5.dp, ChampionshipGold)
    } else {
        BorderStroke(1.dp, StadiumBorder)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("bracket_card_${fixture.id}"),
        shape = RoundedCornerShape(14.dp),
        border = borderStroke,
        colors = CardDefaults.cardColors(
            containerColor = if (isGrandFinal) StadiumSurfaceElevated else StadiumSurface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Status and Match Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fixture.stage?.uppercase() ?: "KNOCKOUT MATCH",
                    color = if (isGrandFinal) ChampionshipGold else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                MatchStatusBadge(status = fixture.status, minute = fixture.minute)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Home Team Row
            BracketTeamRow(
                teamName = fixture.home,
                score = fixture.homeScore,
                isWinner = homeWon,
                isCompleted = fixture.isCompleted
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Away Team Row
            BracketTeamRow(
                teamName = fixture.away,
                score = fixture.awayScore,
                isWinner = awayWon,
                isCompleted = fixture.isCompleted
            )

            // Date / Venue footer
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${fixture.date ?: "TBD"} • ${fixture.time ?: "TBD"}",
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Text(
                    text = fixture.venue ?: "BBIT Ground",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun BracketTeamRow(
    teamName: String,
    score: Int?,
    isWinner: Boolean,
    isCompleted: Boolean
) {
    Surface(
        color = if (isWinner) ChampionshipGold.copy(alpha = 0.12f) else StadiumSurfaceElevated,
        shape = RoundedCornerShape(8.dp),
        border = if (isWinner) BorderStroke(1.dp, ChampionshipGold.copy(alpha = 0.4f)) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                TeamCrestBadge(teamName = teamName, size = 28)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = teamName,
                    color = if (isWinner) ChampionshipGold else TextCream,
                    fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (score != null) {
                Text(
                    text = "$score",
                    color = if (isWinner) ChampionshipGold else TextCream,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

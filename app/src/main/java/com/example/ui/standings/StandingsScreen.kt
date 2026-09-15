package com.example.ui.standings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Standing
import com.example.ui.TournamentViewModel
import com.example.ui.components.EmptyTournamentState
import com.example.ui.components.OfflineErrorBanner
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
fun StandingsScreen(
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val standings by viewModel.computedStandings.collectAsState()

    val statsHorizontalScroll = rememberScrollState()

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
            // Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "THE LEAGUE TABLE",
                        color = ElectricLime,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Standings & Points",
                        color = TextCream,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                IconButton(
                    onClick = { viewModel.refresh() },
                    modifier = Modifier.testTag("refresh_standings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = ElectricLime
                    )
                }
            }

            // Offline Banner
            if (uiState.isOffline) {
                OfflineErrorBanner(onRetry = { viewModel.refresh() })
            }

            // Legend banner: Top 2 qualify for knockouts
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = ChampionshipGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Top 2 teams advance to the Knockout Semi-Finals",
                        color = ChampionshipGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (uiState.isLoading && standings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ElectricLime)
                }
            } else if (standings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyTournamentState(
                        title = "No standings available",
                        message = "League points and standings will appear here once matches are scheduled or completed."
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("standings_table_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(StadiumForest)
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Fixed POS & TEAM header
                            Row(
                                modifier = Modifier.width(160.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#",
                                    color = ChampionshipGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(36.dp)
                                )
                                Text(
                                    text = "COLLEGE TEAM",
                                    color = ChampionshipGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            // Scrollable stats headers: P, W, D, L, GF, GA, GD, PTS
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(statsHorizontalScroll)
                                    .padding(end = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatHeaderCell("P", 32)
                                StatHeaderCell("W", 32)
                                StatHeaderCell("D", 32)
                                StatHeaderCell("L", 32)
                                StatHeaderCell("GF", 36)
                                StatHeaderCell("GA", 36)
                                StatHeaderCell("GD", 38)
                                StatHeaderCell("PTS", 42, isHighlight = true)
                            }
                        }

                        // Table Rows
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 80.dp)
                        ) {
                            itemsIndexed(standings) { index, row ->
                                val isTopQualifier = index < 2
                                val rowBg = if (isTopQualifier) {
                                    ChampionshipGold.copy(alpha = 0.08f)
                                } else if (index % 2 == 0) {
                                    PitchDarkEmerald.copy(alpha = 0.4f)
                                } else {
                                    Color.Transparent
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(rowBg)
                                        .border(
                                            androidx.compose.foundation.BorderStroke(
                                                0.5.dp,
                                                if (isTopQualifier) ChampionshipGold.copy(alpha = 0.25f) else StadiumBorder.copy(alpha = 0.2f)
                                            )
                                        )
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Fixed Rank & Team
                                    Row(
                                        modifier = Modifier.width(160.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isTopQualifier) "0${index + 1}★" else String.format("%02d", index + 1),
                                            color = if (isTopQualifier) ChampionshipGold else TextMuted,
                                            fontSize = 11.sp,
                                            fontWeight = if (isTopQualifier) FontWeight.Bold else FontWeight.Normal,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.width(36.dp)
                                        )
                                        Text(
                                            text = row.team,
                                            color = if (isTopQualifier) TextCream else TextCream.copy(alpha = 0.9f),
                                            fontSize = 12.sp,
                                            fontWeight = if (isTopQualifier) FontWeight.Bold else FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(start = 4.dp)
                                        )
                                    }

                                    // Scrollable stats: P, W, D, L, GF, GA, GD, PTS
                                    Row(
                                        modifier = Modifier
                                            .horizontalScroll(statsHorizontalScroll)
                                            .padding(end = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        StatDataCell("${row.played ?: 0}", 32)
                                        StatDataCell("${row.wins ?: 0}", 32)
                                        StatDataCell("${row.draws ?: 0}", 32)
                                        StatDataCell("${row.losses ?: 0}", 32)
                                        StatDataCell("${row.gf ?: 0}", 36)
                                        StatDataCell("${row.ga ?: 0}", 36)
                                        val gd = row.goalDifference
                                        val gdText = if (gd > 0) "+$gd" else "$gd"
                                        StatDataCell(gdText, 38, color = if (gd > 0) ElectricLime else TextMuted)
                                        StatDataCell("${row.totalPoints}", 42, isHighlight = true)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatHeaderCell(text: String, widthDp: Int, isHighlight: Boolean = false) {
    Text(
        text = text,
        color = if (isHighlight) ElectricLime else ChampionshipGold,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.width(widthDp.dp)
    )
}

@Composable
private fun StatDataCell(
    text: String,
    widthDp: Int,
    isHighlight: Boolean = false,
    color: Color = TextCream
) {
    Text(
        text = text,
        color = if (isHighlight) ElectricLime else color,
        fontSize = 12.sp,
        fontWeight = if (isHighlight) FontWeight.Black else FontWeight.Normal,
        textAlign = TextAlign.Center,
        modifier = Modifier.width(widthDp.dp)
    )
}

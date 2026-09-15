package com.example.ui.fixtures

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Fixture
import com.example.ui.TournamentViewModel
import com.example.ui.components.EmptyTournamentState
import com.example.ui.components.MatchCard
import com.example.ui.components.OfflineErrorBanner
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PitchDarkEmerald
import com.example.ui.theme.StadiumBorder
import com.example.ui.theme.StadiumForest
import com.example.ui.theme.StadiumSurface
import com.example.ui.theme.StadiumSurfaceElevated
import com.example.ui.theme.TextCream
import com.example.ui.theme.TextMuted

enum class FixtureFilter(val title: String) {
    ALL("All Matches"),
    LIVE("Live"),
    UPCOMING("Upcoming"),
    COMPLETED("Completed")
}

@Composable
fun FixturesScreen(
    viewModel: TournamentViewModel,
    onSelectFixture: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf(FixtureFilter.ALL) }

    val allFixtures = uiState.content.fixtures

    val filteredFixtures = remember(allFixtures, selectedFilter) {
        when (selectedFilter) {
            FixtureFilter.ALL -> allFixtures
            FixtureFilter.LIVE -> allFixtures.filter { it.isLive }
            FixtureFilter.UPCOMING -> allFixtures.filter { it.isScheduled }
            FixtureFilter.COMPLETED -> allFixtures.filter { it.isCompleted }
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
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MATCH CENTRE",
                        color = ElectricLime,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Fixtures & Results",
                        color = TextCream,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                IconButton(
                    onClick = { viewModel.refresh() },
                    modifier = Modifier.testTag("refresh_fixtures_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = ElectricLime
                    )
                }
            }

            // Offline banner
            if (uiState.isOffline) {
                OfflineErrorBanner(onRetry = { viewModel.refresh() })
            }

            // Filter Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(FixtureFilter.values()) { filter ->
                    val isSelected = filter == selectedFilter
                    Surface(
                        color = if (isSelected) ElectricLime else StadiumSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) ElectricLime else StadiumBorder
                        ),
                        modifier = Modifier
                            .clickable { selectedFilter = filter }
                            .testTag("filter_${filter.name.lowercase()}")
                    ) {
                        Text(
                            text = filter.title,
                            color = if (isSelected) PitchBlack else TextCream,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fixtures List
            if (uiState.isLoading && allFixtures.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ElectricLime)
                }
            } else if (filteredFixtures.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyTournamentState(
                        title = "No matches found",
                        message = when (selectedFilter) {
                            FixtureFilter.LIVE -> "No matches are live at the moment."
                            FixtureFilter.UPCOMING -> "No upcoming matches currently scheduled."
                            FixtureFilter.COMPLETED -> "No completed matches recorded yet."
                            FixtureFilter.ALL -> "No fixtures found in the tournament database."
                        }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredFixtures, key = { it.id }) { fixture ->
                        MatchCard(
                            fixture = fixture,
                            onClick = { onSelectFixture(fixture.id) }
                        )
                    }
                }
            }
        }
    }
}

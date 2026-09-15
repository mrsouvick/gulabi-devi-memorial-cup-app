package com.example.ui.teams

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.Team
import com.example.ui.TournamentViewModel
import com.example.ui.components.EmptyTournamentState
import com.example.ui.components.OfflineErrorBanner
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
fun TeamsScreen(
    viewModel: TournamentViewModel,
    onSelectTeam: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val allTeams = uiState.content.teams

    var searchQuery by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf("All") }

    val availableGroups = remember(allTeams) {
        val groups = allTeams.mapNotNull { it.group }.filter { it.isNotBlank() }.distinct().sorted()
        listOf("All") + groups
    }

    val filteredTeams = remember(allTeams, searchQuery, selectedGroup) {
        allTeams.filter { team ->
            val matchesGroup = selectedGroup == "All" || team.group.equals(selectedGroup, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    team.name.contains(searchQuery, ignoreCase = true) ||
                    (team.shortName?.contains(searchQuery, ignoreCase = true) == true) ||
                    (team.city?.contains(searchQuery, ignoreCase = true) == true)
            matchesGroup && matchesSearch
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
                        text = "PARTICIPATING COLLEGES",
                        color = ElectricLime,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Tournament Teams",
                        color = TextCream,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                IconButton(
                    onClick = { viewModel.refresh() },
                    modifier = Modifier.testTag("refresh_teams_button")
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

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search college or team...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricLime,
                    unfocusedBorderColor = StadiumBorder,
                    focusedContainerColor = StadiumSurface,
                    unfocusedContainerColor = StadiumSurface,
                    focusedTextColor = TextCream,
                    unfocusedTextColor = TextCream
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("team_search_field")
            )

            // Group Filter chips
            if (availableGroups.size > 1) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableGroups) { group ->
                        val isSelected = group == selectedGroup
                        Surface(
                            color = if (isSelected) ElectricLime else StadiumSurface,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) ElectricLime else StadiumBorder
                            ),
                            modifier = Modifier
                                .clickable { selectedGroup = group }
                                .testTag("group_filter_$group")
                        ) {
                            Text(
                                text = group,
                                color = if (isSelected) PitchBlack else TextCream,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Teams List
            if (uiState.isLoading && allTeams.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ElectricLime)
                }
            } else if (filteredTeams.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyTournamentState(
                        title = "No teams found",
                        message = "Try changing your search keywords or group filter."
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTeams, key = { it.id.ifBlank { it.name } }) { team ->
                        TeamCardItem(
                            team = team,
                            onClick = { onSelectTeam(team.id.ifBlank { team.name }) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamCardItem(
    team: Team,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("team_item_${team.id.ifBlank { team.name }}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StadiumSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamCrestBadge(
                teamName = team.name,
                shortCode = team.shortName,
                size = 48
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = team.name,
                    color = TextCream,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!team.city.isNullOrBlank()) {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = team.city,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            if (!team.group.isNullOrBlank()) {
                Surface(
                    color = StadiumForest,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = team.group,
                        color = ChampionshipGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View Details",
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

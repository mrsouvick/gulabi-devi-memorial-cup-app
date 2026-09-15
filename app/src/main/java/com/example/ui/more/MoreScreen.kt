package com.example.ui.more

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TournamentViewModel
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

enum class MoreSubPage {
    MENU,
    TOURNAMENT_INFO,
    VENUE_MAP,
    CHAMPIONS,
    DIGNITARIES_CONTACT,
    APP_DIAGNOSTICS
}

@Composable
fun MoreScreen(
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var currentPage by remember { mutableStateOf(MoreSubPage.MENU) }

    val settings = uiState.content.settings

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
                        text = "BBIT SPORTS & TOURNAMENT",
                        color = ElectricLime,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = when (currentPage) {
                            MoreSubPage.MENU -> "Tournament Guide"
                            MoreSubPage.TOURNAMENT_INFO -> "Tournament Details"
                            MoreSubPage.VENUE_MAP -> "Venue & Location"
                            MoreSubPage.CHAMPIONS -> "Past Champions"
                            MoreSubPage.DIGNITARIES_CONTACT -> "Committee & Contacts"
                            MoreSubPage.APP_DIAGNOSTICS -> "App & Sync Diagnostics"
                        },
                        color = TextCream,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                if (currentPage != MoreSubPage.MENU) {
                    IconButton(
                        onClick = { currentPage = MoreSubPage.MENU },
                        modifier = Modifier.testTag("more_back_to_menu")
                    ) {
                        Text("BACK", color = ElectricLime, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // Offline banner
            if (uiState.isOffline) {
                OfflineErrorBanner(onRetry = { viewModel.refresh() })
            }

            when (currentPage) {
                MoreSubPage.MENU -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            MoreMenuItem(
                                icon = Icons.Default.Info,
                                title = "Tournament Information",
                                subtitle = "Format, rules, schedule, and eligibility",
                                accentColor = ElectricLime,
                                onClick = { currentPage = MoreSubPage.TOURNAMENT_INFO }
                            )
                        }
                        item {
                            MoreMenuItem(
                                icon = Icons.Default.LocationOn,
                                title = "Venue & Directions",
                                subtitle = "BBIT Football Ground, Budge Budge",
                                accentColor = ChampionshipGold,
                                onClick = { currentPage = MoreSubPage.VENUE_MAP }
                            )
                        }
                        item {
                            MoreMenuItem(
                                icon = Icons.Default.EmojiEvents,
                                title = "Past Champions",
                                subtitle = "Roll of Honor & Previous Editions",
                                accentColor = ChampionshipGold,
                                onClick = { currentPage = MoreSubPage.CHAMPIONS }
                            )
                        }
                        item {
                            MoreMenuItem(
                                icon = Icons.Default.Person,
                                title = "Committee & Contacts",
                                subtitle = "Dignitaries, patrons, and coordinators",
                                accentColor = ElectricLime,
                                onClick = { currentPage = MoreSubPage.DIGNITARIES_CONTACT }
                            )
                        }
                        item {
                            MoreMenuItem(
                                icon = Icons.Default.Security,
                                title = "App & Data Sync",
                                subtitle = "Live data connection status and cache",
                                accentColor = TextMuted,
                                onClick = { currentPage = MoreSubPage.APP_DIAGNOSTICS }
                            )
                        }
                    }
                }

                MoreSubPage.TOURNAMENT_INFO -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            InfoCard(
                                title = "ABOUT THE TOURNAMENT",
                                content = settings.about
                            )
                        }
                        item {
                            InfoCard(
                                title = "FORMAT & RULES",
                                content = "• Format: ${settings.format}\n• Match Type: ${settings.matchType}\n• Entry Fee: ₹${settings.entryFee} per college team\n• Status: ${settings.status}\n• Dates: ${settings.date}\n• Regulations: All matches strictly officiated under IFA West Bengal collegiate football rules."
                            )
                        }
                        if (!settings.registrationNote.isNullOrBlank()) {
                            item {
                                InfoCard(
                                    title = "REGISTRATION INSTRUCTIONS",
                                    content = settings.registrationNote
                                )
                            }
                        }
                    }
                }

                MoreSubPage.VENUE_MAP -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                                border = BorderStroke(1.dp, StadiumBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = ElectricLime,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = settings.venue,
                                            color = TextCream,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = settings.address,
                                        color = TextMuted,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            val mapIntent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(settings.mapLink ?: "geo:0,0?q=Budge+Budge+Institute+of+Technology")
                                            )
                                            context.startActivity(mapIntent)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ElectricLime,
                                            contentColor = PitchBlack
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("open_map_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Map,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Open in Google Maps",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                MoreSubPage.CHAMPIONS -> {
                    val champions = uiState.content.champions
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(champions) { champ ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                                border = BorderStroke(1.dp, ChampionshipGold.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = ChampionshipGold.copy(alpha = 0.2f),
                                            shape = CircleShape,
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.EmojiEvents,
                                                    contentDescription = null,
                                                    tint = ChampionshipGold,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = champ.winner,
                                                color = ChampionshipGold,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "Runner-Up: ${champ.runnerUp}",
                                                color = TextMuted,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Surface(
                                        color = StadiumForest,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = champ.year,
                                            color = TextCream,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                MoreSubPage.DIGNITARIES_CONTACT -> {
                    val contacts = uiState.content.contacts
                    val dignitaries = uiState.content.dignitaries
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (dignitaries.isNotEmpty()) {
                            item {
                                Text(
                                    text = "PATRONS & DIGNITARIES",
                                    color = ElectricLime,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            items(dignitaries) { dig ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                                    border = BorderStroke(1.dp, StadiumBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(36.dp),
                                            shape = CircleShape,
                                            color = StadiumSurfaceElevated
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = ElectricLime,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = dig.name, color = TextCream, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(text = dig.role, color = TextMuted, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }

                        if (contacts.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "ORGANIZING COMMITTEE & HELP DESK",
                                    color = ElectricLime,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            items(contacts) { contact ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                                    border = BorderStroke(1.dp, StadiumBorder)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = contact.name, color = TextCream, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(text = "${contact.role} • ${contact.phone}", color = TextMuted, fontSize = 11.sp)
                                        }
                                        IconButton(
                                            onClick = {
                                                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                                                context.startActivity(callIntent)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Call,
                                                contentDescription = "Call",
                                                tint = ElectricLime
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                MoreSubPage.APP_DIAGNOSTICS -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            InfoCard(
                                title = "LIVE DATA SYNC",
                                content = "• Connection: Realtime Stream\n• Status: ${if (uiState.isRealtimeConnected) "Connected (Live)" else "Offline / Reconnecting"}\n• Single Source of Truth: Tournament Admin Panel\n• Offline Cache: Local Device Storage"
                            )
                        }
                        item {
                            Button(
                                onClick = { viewModel.refresh() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricLime,
                                    contentColor = PitchBlack
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("force_sync_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Force Sync Tournament Data",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MoreMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("more_menu_${title.replace(" ", "_").lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StadiumSurface),
        border = BorderStroke(1.dp, StadiumBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = StadiumSurfaceElevated
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextCream,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun InfoCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StadiumSurface),
        border = BorderStroke(1.dp, StadiumBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = ChampionshipGold,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                color = TextCream,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}

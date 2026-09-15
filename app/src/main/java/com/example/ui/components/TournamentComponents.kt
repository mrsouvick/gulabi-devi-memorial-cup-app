package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Fixture
import com.example.ui.theme.ChampionshipGold
import com.example.ui.theme.ElectricLime
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
fun LivePulseDot(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .size(10.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(MatchLiveCoral.copy(alpha = alpha))
    )
}

@Composable
fun TeamCrestBadge(
    teamName: String,
    modifier: Modifier = Modifier,
    size: Int = 44,
    shortCode: String? = null
) {
    val code = shortCode?.takeIf { it.isNotBlank() }
        ?: teamName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(3).joinToString("")
            .ifBlank { "FC" }

    Surface(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape),
        color = StadiumSurfaceElevated,
        border = BorderStroke(1.5.dp, StadiumBorder),
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = code.uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = (size * 0.33).sp,
                color = ElectricLime,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun MatchStatusBadge(
    status: String?,
    minute: String?,
    modifier: Modifier = Modifier
) {
    val isLive = status?.equals("live", ignoreCase = true) == true
    val isCompleted = status?.equals("completed", ignoreCase = true) == true

    when {
        isLive -> {
            Surface(
                color = MatchLiveCoral.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, MatchLiveCoral),
                shape = RoundedCornerShape(12.dp),
                modifier = modifier
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LivePulseDot()
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (!minute.isNullOrBlank()) "LIVE ($minute)" else "LIVE MATCH",
                        color = MatchLiveCoral,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
        isCompleted -> {
            Surface(
                color = StadiumForest.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, StadiumBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = modifier
            ) {
                Text(
                    text = "FINAL SCORE",
                    color = ElectricLime,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        else -> {
            Surface(
                color = PitchDarkEmerald,
                border = BorderStroke(1.dp, StadiumBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = modifier
            ) {
                Text(
                    text = "SCHEDULED",
                    color = TextMuted,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun MatchCard(
    fixture: Fixture,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLive = fixture.isLive
    val cardBorder = if (isLive) {
        BorderStroke(1.5.dp, MatchLiveCoral.copy(alpha = 0.8f))
    } else {
        BorderStroke(1.dp, StadiumBorder)
    }

    val backgroundBrush = if (isLive) {
        Brush.verticalGradient(
            colors = listOf(
                MatchLiveCoral.copy(alpha = 0.12f),
                StadiumSurface
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                StadiumSurface,
                PitchDarkEmerald
            )
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("match_card_${fixture.id}"),
        shape = RoundedCornerShape(16.dp),
        border = cardBorder,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.background(backgroundBrush)) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header: Stage & Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = StadiumSurfaceElevated,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = fixture.stage?.uppercase() ?: "MATCH",
                            color = ChampionshipGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    MatchStatusBadge(status = fixture.status, minute = fixture.minute)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Teams and Scores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Home Team
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TeamCrestBadge(teamName = fixture.home, size = 48)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = fixture.home,
                            color = TextCream,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Score or VS center box
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (fixture.isLive || fixture.isCompleted) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${fixture.homeScore ?: 0}",
                                    color = if (isLive) MatchLiveCoral else ElectricLime,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = " - ",
                                    color = TextMuted,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                Text(
                                    text = "${fixture.awayScore ?: 0}",
                                    color = if (isLive) MatchLiveCoral else ElectricLime,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        } else {
                            Surface(
                                color = StadiumForest,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "VS",
                                    color = ChampionshipGold,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Away Team
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TeamCrestBadge(teamName = fixture.away, size = 48)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = fixture.away,
                            color = TextCream,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Live note ticker if present
                if (!fixture.liveNote.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = PitchDarkEmerald.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsSoccer,
                                contentDescription = null,
                                tint = ElectricLime,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = fixture.liveNote,
                                color = ElectricLime,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer: Date/Time and Venue
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${fixture.date ?: "TBD"} • ${fixture.time ?: "TBD"}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = fixture.venue ?: "BBIT Ground",
                            color = TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OfflineErrorBanner(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MatchLiveCoral.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, MatchLiveCoral.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("offline_error_banner")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "No internet connection",
                    color = TextCream,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "Showing offline cached tournament data",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatchLiveCoral,
                    contentColor = PitchBlack
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("retry_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Retry", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun EmptyTournamentState(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = StadiumSurfaceElevated,
            border = BorderStroke(1.dp, StadiumBorder)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.SportsSoccer,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            color = TextCream,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            color = TextMuted,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

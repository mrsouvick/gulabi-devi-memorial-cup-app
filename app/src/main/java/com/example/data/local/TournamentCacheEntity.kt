package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tournament_cache")
data class CachedTournamentContent(
    @PrimaryKey val id: String = "main",
    val jsonContent: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

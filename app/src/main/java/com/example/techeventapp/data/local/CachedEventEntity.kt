package com.example.techeventapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.techeventapp.data.model.AgendaItem
import com.example.techeventapp.data.model.Speaker

@Entity(tableName = "cached_events")
data class CachedEventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val date: String,
    val bannerUrl: String,
    val logoUrl: String,
    val status: String,
    val description: String,
    val speakers: List<Speaker>,
    val agenda: List<AgendaItem>,
    val location: String
)

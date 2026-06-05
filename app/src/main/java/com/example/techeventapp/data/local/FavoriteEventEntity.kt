package com.example.techeventapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_events")
data class FavoriteEventEntity(
    @PrimaryKey val eventId: String
)

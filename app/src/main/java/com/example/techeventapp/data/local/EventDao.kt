package com.example.techeventapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("INSERT OR IGNORE INTO favorite_events (eventId) VALUES (:eventId)")
    suspend fun insertFavorite(eventId: String)

    @Query("DELETE FROM favorite_events WHERE eventId = :eventId")
    suspend fun deleteFavorite(eventId: String)

    @Query("SELECT * FROM favorite_events")
    fun getFavorites(): Flow<List<FavoriteEventEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_events WHERE eventId = :eventId)")
    suspend fun isFavorite(eventId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedEvents(events: List<CachedEventEntity>)

    @Query("SELECT * FROM cached_events")
    fun getCachedEventsFlow(): Flow<List<CachedEventEntity>>

    @Query("SELECT * FROM cached_events")
    suspend fun getCachedEvents(): List<CachedEventEntity>

    @Query("DELETE FROM cached_events")
    suspend fun clearCachedEvents()
}

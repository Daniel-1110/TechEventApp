package com.example.techeventapp.data.local

import androidx.room.TypeConverter
import com.example.techeventapp.data.model.AgendaItem
import com.example.techeventapp.data.model.Speaker
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromSpeakersList(value: List<Speaker>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toSpeakersList(value: String): List<Speaker> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromAgendaList(value: List<AgendaItem>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toAgendaList(value: String): List<AgendaItem> {
        return Json.decodeFromString(value)
    }
}

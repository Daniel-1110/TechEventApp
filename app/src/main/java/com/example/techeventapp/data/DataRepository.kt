package com.example.techeventapp.data

import com.example.techeventapp.data.local.AppDatabase
import com.example.techeventapp.data.local.CachedEventEntity
import com.example.techeventapp.data.model.Event
import com.example.techeventapp.data.network.EventService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import java.nio.charset.StandardCharsets

interface DataRepository {
    val events: Flow<List<Event>>
    val favoriteEventIds: Flow<Set<String>>
    val isOfflineFlow: StateFlow<Boolean>

    suspend fun refreshEvents()
    suspend fun toggleFavorite(eventId: String)
}

class DefaultDataRepository(
    private val database: AppDatabase
) : DataRepository {

    private val eventDao = database.eventDao()
    private val json = Json { ignoreUnknownKeys = true }

    // 1. Creamos un cliente OkHttp con un interceptor que traduce el texto roto (ISO_8859_1) a UTF-8 limpio
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalResponse = chain.proceed(chain.request())
            val responseBody = originalResponse.body

            if (originalResponse.isSuccessful && responseBody != null) {
                // Leemos los bytes crudos directamente descargados de internet
                val bytes = responseBody.bytes()

                // Forzamos la interpretación del texto a strings legibles
                val utf8String = String(bytes, StandardCharsets.ISO_8859_1)
                val cleanBytes = utf8String.toByteArray(StandardCharsets.UTF_8)

                // Reconstruimos el cuerpo de la respuesta con los caracteres ya reparados
                val newBody = cleanBytes.toResponseBody(responseBody.contentType())
                originalResponse.newBuilder().body(newBody).build()
            } else {
                originalResponse
            }
        }
        .build()

    // 2. Le inyectamos el cliente corregido al constructor de Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.pastes.dev/")
        .client(okHttpClient) // <--- Esta línea aplica el limpiador de texto automáticamente
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val eventService = retrofit.create(EventService::class.java)

    private val _isOfflineFlow = MutableStateFlow(false)
    override val isOfflineFlow: StateFlow<Boolean> = _isOfflineFlow.asStateFlow()

    // Expose cached events mapped to domain model
    override val events: Flow<List<Event>> = eventDao.getCachedEventsFlow().map { cachedList ->
        cachedList.map { cached ->
            Event(
                id = cached.id,
                title = cached.title,
                date = cached.date,
                bannerUrl = cached.bannerUrl,
                logoUrl = cached.logoUrl,
                status = cached.status,
                description = cached.description,
                speakers = cached.speakers,
                agenda = cached.agenda,
                location = cached.location
            )
        }
    }

    // Expose favorites as a Set for fast check
    override val favoriteEventIds: Flow<Set<String>> = eventDao.getFavorites().map { list ->
        list.map { it.eventId }.toSet()
    }

    override suspend fun refreshEvents() {
        withContext(Dispatchers.IO) {
            try {
                // Fetch from network
                val networkEvents = eventService.getEvents()

                // Map to cached entity list
                val cachedEntities = networkEvents.map { event ->
                    CachedEventEntity(
                        id = event.id,
                        title = event.title,
                        date = event.date,
                        bannerUrl = event.bannerUrl,
                        logoUrl = event.logoUrl,
                        status = event.status,
                        description = event.description,
                        speakers = event.speakers,
                        agenda = event.agenda,
                        location = event.location
                    )
                }

                // Update local cache
                eventDao.clearCachedEvents()
                eventDao.insertCachedEvents(cachedEntities)

                // Emit offline = false
                _isOfflineFlow.value = false
            } catch (e: Exception) {
                // Intercept the exception and fall back to local cached events
                // Notify UI of offline mode
                _isOfflineFlow.value = true
            }
        }
    }

    override suspend fun toggleFavorite(eventId: String) {
        withContext(Dispatchers.IO) {
            if (eventDao.isFavorite(eventId)) {
                eventDao.deleteFavorite(eventId)
            } else {
                eventDao.insertFavorite(eventId)
            }
        }
    }
}
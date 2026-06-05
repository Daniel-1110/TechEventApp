package com.example.techeventapp.data.network

import com.example.techeventapp.data.model.Event
import retrofit2.http.GET
import retrofit2.http.Headers

interface EventService {

    // Forzamos al cliente HTTP a interpretar la respuesta JSON estrictamente en UTF-8
    @Headers("Content-Type: application/json; charset=utf-8")
    @GET("eJGSh2uNrf")
    suspend fun getEvents(): List<Event>
}
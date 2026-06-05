package com.example.techeventapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Event(
  val id: String,
  val title: String,
  val date: String,
  val bannerUrl: String,
  val logoUrl: String,
  val status: String, // e.g. "Cupos Disponibles" or "Agotado"
  val description: String,
  val speakers: List<Speaker>,
  val agenda: List<AgendaItem>,
  val location: String
)

@Serializable
data class Speaker(
  val name: String,
  val role: String
)

@Serializable
data class AgendaItem(
  val time: String,
  val activity: String
)

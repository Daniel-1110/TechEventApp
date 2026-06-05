package com.example.techeventapp

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Catalog : NavKey

@Serializable
data class Detail(val eventId: String) : NavKey

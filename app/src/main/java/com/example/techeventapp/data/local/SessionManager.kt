package com.example.techeventapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session_preferences")

/**
 * Clase encargada de gestionar el estado de la sesión del usuario utilizando DataStore.
 */
class SessionManager(private val context: Context) {
    companion object {
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }

    /**
     * Flujo que indica si el usuario ha iniciado sesión.
     */
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY] ?: false
    }

    /**
     * Actualiza el estado de inicio de sesión.
     * @param isLoggedIn true si el usuario inició sesión, false en caso contrario.
     */
    suspend fun setLoginStatus(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = isLoggedIn
        }
    }
}

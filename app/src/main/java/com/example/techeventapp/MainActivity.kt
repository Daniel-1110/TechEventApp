package com.example.techeventapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.techeventapp.data.DefaultDataRepository
import com.example.techeventapp.data.local.AppDatabase
import com.example.techeventapp.data.local.SessionManager // <-- Agregamos tu import real
import com.example.techeventapp.data.local.ThemePreferences
import com.example.techeventapp.theme.TechEventAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val database = AppDatabase.getDatabase(this)
    val repository = DefaultDataRepository(database)
    val themePreferences = ThemePreferences(this)
    val sessionManager = SessionManager(this) // <-- Instanciamos tu manager de sesión con el contexto

    enableEdgeToEdge()
    setContent {
      val isDarkMode by themePreferences.isDarkMode.collectAsStateWithLifecycle(initialValue = false)
      val coroutineScope = rememberCoroutineScope()

      val configuration = LocalConfiguration.current
      val isExpanded = configuration.screenWidthDp >= 600

      TechEventAppTheme(darkTheme = isDarkMode) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          MainNavigation(
            repository = repository,
            sessionManager = sessionManager, // <-- INYECTAMOS EL PARAMETRO MANDATORIO AQUÍ
            isExpanded = isExpanded,
            isDarkMode = isDarkMode,
            onThemeToggle = { targetDark ->
              coroutineScope.launch {
                themePreferences.setDarkMode(targetDark)
              }
            }
          )
        }
      }
    }
  }
}
package com.example.techeventapp

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.techeventapp.data.DataRepository
import com.example.techeventapp.data.local.SessionManager
import com.example.techeventapp.ui.catalog.CatalogScreen
import com.example.techeventapp.ui.catalog.CatalogViewModel
import com.example.techeventapp.ui.detail.DetailScreen
import com.example.techeventapp.ui.detail.DetailViewModel
import com.example.techeventapp.ui.responsive.ListDetailScreen

// Importaciones explícitas apuntando a tu paquete con L mayúscula
import com.example.techeventapp.ui.Login.LoginScreen
import com.example.techeventapp.ui.Login.LoginViewModel

@Composable
fun MainNavigation(
    repository: DataRepository,
    sessionManager: SessionManager,
    isExpanded: Boolean,
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUserLoggedIn by sessionManager.isLoggedIn.collectAsState(initial = false)
    val catalogViewModel: CatalogViewModel = viewModel { CatalogViewModel(repository) }

    if (isExpanded) {
        ListDetailScreen(
            catalogViewModel = catalogViewModel,
            detailViewModelFactory = { eventId -> DetailViewModel(repository, eventId) },
            isDarkMode = isDarkMode,
            onThemeToggle = onThemeToggle,
            modifier = modifier
        )
    } else {
        val startDestination = if (isUserLoggedIn) Catalog else Login
        val backStack = rememberNavBackStack(startDestination)

        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider<NavKey> {

                // --- PANTALLA DE LOGIN ---
                // Forzamos el paquete raíz para que no se confunda con el paquete ui.Login
                entry<com.example.techeventapp.Login> {
                    val loginViewModel: LoginViewModel = viewModel { LoginViewModel(sessionManager) }
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = {
                            backStack.removeLastOrNull()
                            backStack.add(Catalog)
                        },
                        modifier = Modifier.safeDrawingPadding()
                    )
                }

                // --- PANTALLA DE CATÁLOGO ---
                entry<com.example.techeventapp.Catalog> {
                    CatalogScreen(
                        viewModel = catalogViewModel,
                        onEventClick = { eventId -> backStack.add(Detail(eventId)) },
                        isDarkMode = isDarkMode,
                        onThemeToggle = onThemeToggle,
                        modifier = Modifier.safeDrawingPadding()
                    )
                }

                // --- PANTALLA DE DETALLE ---
                entry<com.example.techeventapp.Detail> {
                    val eventId = this.eventId
                    val detailViewModel = viewModel(key = eventId) { DetailViewModel(repository, eventId) }
                    DetailScreen(
                        viewModel = detailViewModel,
                        onBackClick = { backStack.removeLastOrNull() }
                    )
                }
            }
        )
    }
}
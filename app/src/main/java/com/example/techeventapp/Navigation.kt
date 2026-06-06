package com.example.techeventapp

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.techeventapp.data.DataRepository
import com.example.techeventapp.ui.catalog.CatalogScreen
import com.example.techeventapp.ui.catalog.CatalogViewModel
import com.example.techeventapp.ui.detail.DetailScreen
import com.example.techeventapp.ui.detail.DetailViewModel
import com.example.techeventapp.ui.responsive.ListDetailScreen

@Composable
fun MainNavigation(
    repository: DataRepository,
    isExpanded: Boolean,
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
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
        val backStack = rememberNavBackStack(Catalog)

        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<Catalog> {
                    CatalogScreen(
                        viewModel = catalogViewModel,
                        onEventClick = { eventId -> backStack.add(Detail(eventId)) },
                        isDarkMode = isDarkMode,
                        onThemeToggle = onThemeToggle,
                        modifier = Modifier.safeDrawingPadding()
                    )
                }
                entry<Detail> { entry ->
                    val eventId = entry.eventId
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

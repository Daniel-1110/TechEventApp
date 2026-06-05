package com.example.techeventapp.ui.responsive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.techeventapp.data.model.Event
import com.example.techeventapp.ui.catalog.CatalogScreen
import com.example.techeventapp.ui.catalog.CatalogViewModel
import com.example.techeventapp.ui.detail.DetailScreen
import com.example.techeventapp.ui.detail.DetailViewModel
import com.example.techeventapp.ui.state.UIState

@Composable
fun ListDetailScreen(
    catalogViewModel: CatalogViewModel,
    detailViewModelFactory: (String) -> DetailViewModel,
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val catalogState by catalogViewModel.uiState.collectAsStateWithLifecycle()
    var selectedEventId by remember { mutableStateOf<String?>(null) }

    // Auto-select the first event on launch when data succeeds
    if (selectedEventId == null && catalogState is UIState.Success) {
        val events = (catalogState as UIState.Success<List<Event>>).data
        if (events.isNotEmpty()) {
            selectedEventId = events.first().id
        }
    }

    Row(modifier = modifier.fillMaxSize()) {
        // Left Pane (Catalog list)
        Box(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
        ) {
            CatalogScreen(
                viewModel = catalogViewModel,
                onEventClick = { eventId -> selectedEventId = eventId },
                isDarkMode = isDarkMode,
                onThemeToggle = onThemeToggle
            )
        }

        // Vertical divider
        VerticalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))

        // Right Pane (Detail view)
        Box(
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight()
        ) {
            val id = selectedEventId
            if (id != null) {
                androidx.compose.runtime.key(id) {
                    val detailViewModel = remember(id) { detailViewModelFactory(id) }
                    DetailScreen(
                        viewModel = detailViewModel,
                        onBackClick = null // No back button needed in split-screen detail
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Seleccione un evento de la lista para ver sus detalles.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
            }
        }
    }
}

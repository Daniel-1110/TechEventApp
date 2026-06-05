package com.example.techeventapp.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techeventapp.data.DataRepository
import com.example.techeventapp.data.model.Event
import com.example.techeventapp.ui.state.UIState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CatalogViewModel(private val repository: DataRepository) : ViewModel() {

    val uiState: StateFlow<UIState<List<Event>>> = repository.events
        .map<List<Event>, UIState<List<Event>>> { events ->
            if (events.isEmpty()) {
                UIState.Loading
            } else {
                UIState.Success(events)
            }
        }
        .catch { emit(UIState.Error(it.localizedMessage ?: "Unknown error occurred")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UIState.Loading
        )

    val favoriteEventIds: StateFlow<Set<String>> = repository.favoriteEventIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val isOffline: StateFlow<Boolean> = repository.isOfflineFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshEvents()
        }
    }

    fun toggleFavorite(eventId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(eventId)
        }
    }
}

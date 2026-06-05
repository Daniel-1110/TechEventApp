package com.example.techeventapp.ui.detail

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

class DetailViewModel(
    private val repository: DataRepository,
    private val eventId: String
) : ViewModel() {

    val uiState: StateFlow<UIState<Event>> = repository.events
        .map { events ->
            val event = events.find { it.id == eventId }
            if (event != null) {
                UIState.Success(event)
            } else {
                UIState.Error("Event with ID $eventId not found")
            }
        }
        .catch { emit(UIState.Error(it.localizedMessage ?: "Unknown error occurred")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UIState.Loading
        )

    val isFavorite: StateFlow<Boolean> = repository.favoriteEventIds
        .map { it.contains(eventId) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun toggleFavorite() {
        viewModelScope.launch {
            repository.toggleFavorite(eventId)
        }
    }
}

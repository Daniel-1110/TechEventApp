package com.example.techeventapp.ui.Login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techeventapp.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona el estado y la lógica de la pantalla de inicio de sesión.
 *
 * @property sessionManager El gestor de sesiones para persistir el estado de autenticación.
 */
class LoginViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    
    /**
     * Flujo de datos que representa el estado actual de la interfaz de usuario de inicio de sesión.
     */
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Realiza el proceso de inicio de sesión.
     *
     * @param username El nombre de usuario ingresado.
     * @param password La contraseña ingresada.
     */
    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("El usuario y la contraseña no pueden estar vacíos.")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                // Validación de credenciales de prueba
                if (username == "admin" && password == "1234") {
                    sessionManager.setLoginStatus(true)
                    _uiState.value = LoginUiState.Success
                } else {
                    _uiState.value = LoginUiState.Error("Usuario o contraseña incorrectos. Inténtalo de nuevo.")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.localizedMessage ?: "Error desconocido en el servidor.")
            }
        }
    }

    /**
     * Restablece el estado de la interfaz de usuario a [LoginUiState.Idle] si el estado actual es de error.
     */
    fun resetToIdle() {
        if (_uiState.value is LoginUiState.Error) {
            _uiState.value = LoginUiState.Idle
        }
    }
}

package com.example.techeventapp.ui.Login

/**
 * Define los estados posibles de la interfaz de usuario para la pantalla de inicio de sesión.
 */
sealed interface LoginUiState {
    /** Estado inicial o de reposo. */
    object Idle : LoginUiState
    /** Estado que indica que hay una operación de autenticación en curso. */
    object Loading : LoginUiState
    /** Estado que indica que el inicio de sesión fue exitoso. */
    object Success : LoginUiState
    /**
     * Estado que representa un error en el proceso de inicio de sesión.
     * @property message Mensaje descriptivo del error.
     */
    data class Error(val message: String) : LoginUiState
}

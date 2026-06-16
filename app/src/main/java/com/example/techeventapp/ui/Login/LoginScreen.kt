package com.example.techeventapp.ui.Login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Consumo del estado siguiendo el ciclo de vida de Compose de manera segura
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Estados locales para los campos de texto
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Reaccionar inmediatamente cuando el estado pase a Success
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Espacio para el Isotipo/Logotipo de la UMA (Ajusta el ID del recurso según tus assets)
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "UMAevent Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "UMAevent",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Gestión de Eventos Académicos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Campo de Usuario
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        viewModel.resetToIdle() // Limpia errores al escribir
                    },
                    label = { Text("Usuario Institucional") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "Usuario")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is LoginUiState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        viewModel.resetToIdle() // Limpia errores al escribir
                    },
                    label = { Text("Contraseña") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Contraseña")
                    },
                    trailingIcon = {
                        val icon = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(icon, contentDescription = "Cambiar visibilidad")
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is LoginUiState.Loading
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Renderizado condicional de estados visuales (UDF)
                when (uiState) {
                    is LoginUiState.Loading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    is LoginUiState.Error -> {
                        Text(
                            text = (uiState as LoginUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    else -> {}
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.login(username, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = uiState !is LoginUiState.Loading && username.isNotBlank() && password.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
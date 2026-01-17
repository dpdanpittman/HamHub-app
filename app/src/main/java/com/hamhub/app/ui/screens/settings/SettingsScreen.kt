package com.hamhub.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hamhub.app.R
import com.hamhub.app.domain.model.Band
import com.hamhub.app.domain.model.Mode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.saveSettings() },
                        enabled = !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Station Info Section
                SettingsSection(title = "Station Information") {
                    OutlinedTextField(
                        value = uiState.myCallsign,
                        onValueChange = { viewModel.updateCallsign(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("My Callsign") },
                        placeholder = { Text("e.g., W1ABC") },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.myGrid,
                        onValueChange = { viewModel.updateGrid(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("My Grid Square") },
                        placeholder = { Text("e.g., FN31") },
                        leadingIcon = {
                            Icon(Icons.Default.GridOn, contentDescription = null)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters
                        )
                    )
                }

                // Default Values Section
                SettingsSection(title = "Default Values") {
                    OutlinedTextField(
                        value = uiState.defaultPower,
                        onValueChange = { viewModel.updateDefaultPower(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Default Power (watts)") },
                        placeholder = { Text("e.g., 100") },
                        leadingIcon = {
                            Icon(Icons.Default.Power, contentDescription = null)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Default Mode Dropdown
                    var modeExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = modeExpanded,
                        onExpandedChange = { modeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = uiState.defaultMode.ifEmpty { "Not set" },
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            label = { Text("Default Mode") },
                            leadingIcon = {
                                Icon(Icons.Default.Radio, contentDescription = null)
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = modeExpanded,
                            onDismissRequest = { modeExpanded = false }
                        ) {
                            Mode.entries.forEach { mode ->
                                DropdownMenuItem(
                                    text = { Text(mode.display) },
                                    onClick = {
                                        viewModel.updateDefaultMode(mode.display)
                                        modeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Default Band Dropdown
                    var bandExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = bandExpanded,
                        onExpandedChange = { bandExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = uiState.defaultBand.ifEmpty { "Not set" },
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            label = { Text("Default Band") },
                            leadingIcon = {
                                Icon(Icons.Default.Tune, contentDescription = null)
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = bandExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = bandExpanded,
                            onDismissRequest = { bandExpanded = false }
                        ) {
                            Band.entries.forEach { band ->
                                DropdownMenuItem(
                                    text = { Text(band.display) },
                                    onClick = {
                                        viewModel.updateDefaultBand(band.display)
                                        bandExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Appearance Section
                SettingsSection(title = "Appearance") {
                    var themeExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = themeExpanded,
                        onExpandedChange = { themeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = when (uiState.theme) {
                                "light" -> "Light"
                                "dark" -> "Dark"
                                else -> "System Default"
                            },
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            label = { Text("Theme") },
                            leadingIcon = {
                                Icon(
                                    when (uiState.theme) {
                                        "light" -> Icons.Default.LightMode
                                        "dark" -> Icons.Default.DarkMode
                                        else -> Icons.Default.Brightness6
                                    },
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = themeExpanded,
                            onDismissRequest = { themeExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("System Default") },
                                leadingIcon = { Icon(Icons.Default.Brightness6, null) },
                                onClick = {
                                    viewModel.updateTheme("system")
                                    themeExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Light") },
                                leadingIcon = { Icon(Icons.Default.LightMode, null) },
                                onClick = {
                                    viewModel.updateTheme("light")
                                    themeExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Dark") },
                                leadingIcon = { Icon(Icons.Default.DarkMode, null) },
                                onClick = {
                                    viewModel.updateTheme("dark")
                                    themeExpanded = false
                                }
                            )
                        }
                    }
                }

                // About Section
                SettingsSection(title = "About") {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.Radio,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column {
                                    Text(
                                        text = "HamHub",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Version 1.0.0",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Amateur Radio Logging & Tools",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Licensed under CC BY-NC-SA 4.0",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Danger Zone Section
                SettingsSection(title = "Data Management") {
                    var showClearDialog by remember { mutableStateOf(false) }

                    OutlinedButton(
                        onClick = { showClearDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.DeleteForever,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear All Settings")
                    }

                    if (showClearDialog) {
                        AlertDialog(
                            onDismissRequest = { showClearDialog = false },
                            icon = {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            title = { Text("Clear All Settings?") },
                            text = {
                                Text("This will reset all your settings to their default values. This action cannot be undone.")
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        viewModel.clearAllData()
                                        showClearDialog = false
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Clear")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showClearDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
    HorizontalDivider()
}

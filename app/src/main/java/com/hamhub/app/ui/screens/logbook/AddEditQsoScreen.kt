package com.hamhub.app.ui.screens.logbook

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hamhub.app.R
import com.hamhub.app.domain.model.Band
import com.hamhub.app.domain.model.Mode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditQsoScreen(
    qsoId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: LogbookViewModel = hiltViewModel()
) {
    val isEdit = qsoId != null
    val formState by viewModel.formState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(qsoId) {
        if (qsoId != null) {
            viewModel.loadQsoForEdit(qsoId)
        } else {
            viewModel.initNewQso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEdit) stringResource(R.string.action_edit)
                        else stringResource(R.string.action_add_qso)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_cancel)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                val success = viewModel.saveQso(qsoId)
                                if (success) {
                                    onSaved()
                                }
                            }
                        },
                        enabled = formState.isValid
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.action_save)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date and Time row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = formState.date,
                    onValueChange = { viewModel.updateFormField { copy(date = it) } },
                    label = { Text(stringResource(R.string.qso_date)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("YYYY-MM-DD") }
                )
                OutlinedTextField(
                    value = formState.timeUtc,
                    onValueChange = { viewModel.updateFormField { copy(timeUtc = it) } },
                    label = { Text(stringResource(R.string.qso_time)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("HH:MM") }
                )
            }

            // Callsign - most important field
            OutlinedTextField(
                value = formState.callsign,
                onValueChange = { viewModel.updateFormField { copy(callsign = it.uppercase()) } },
                label = { Text(stringResource(R.string.qso_callsign) + " *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters
                ),
                isError = formState.callsign.isBlank()
            )

            // Frequency
            OutlinedTextField(
                value = formState.frequency,
                onValueChange = { viewModel.updateFormField { copy(frequency = it) } },
                label = { Text(stringResource(R.string.qso_frequency)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                suffix = { Text("MHz") }
            )

            // Band dropdown
            BandDropdown(
                selectedBand = formState.band,
                onBandSelected = { viewModel.updateFormField { copy(band = it) } }
            )

            // Mode dropdown
            ModeDropdown(
                selectedMode = formState.mode,
                onModeSelected = { viewModel.updateFormField { copy(mode = it) } }
            )

            // RST Sent/Received row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = formState.rstSent,
                    onValueChange = { viewModel.updateFormField { copy(rstSent = it) } },
                    label = { Text(stringResource(R.string.qso_rst_sent)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = formState.rstReceived,
                    onValueChange = { viewModel.updateFormField { copy(rstReceived = it) } },
                    label = { Text(stringResource(R.string.qso_rst_received)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            HorizontalDivider()

            // Operator info
            Text(
                text = "Operator Info",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = formState.name,
                onValueChange = { viewModel.updateFormField { copy(name = it) } },
                label = { Text(stringResource(R.string.qso_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = formState.qth,
                onValueChange = { viewModel.updateFormField { copy(qth = it) } },
                label = { Text(stringResource(R.string.qso_qth)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = formState.gridSquare,
                    onValueChange = { viewModel.updateFormField { copy(gridSquare = it.uppercase()) } },
                    label = { Text(stringResource(R.string.qso_grid)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters
                    )
                )
                OutlinedTextField(
                    value = formState.state,
                    onValueChange = { viewModel.updateFormField { copy(state = it.uppercase()) } },
                    label = { Text(stringResource(R.string.qso_state)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters
                    )
                )
            }

            OutlinedTextField(
                value = formState.country,
                onValueChange = { viewModel.updateFormField { copy(country = it) } },
                label = { Text(stringResource(R.string.qso_country)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            HorizontalDivider()

            // Station info
            Text(
                text = "Station Info",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = formState.power,
                onValueChange = { viewModel.updateFormField { copy(power = it) } },
                label = { Text(stringResource(R.string.qso_power)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = { Text("W") }
            )

            // QSL checkboxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(
                        checked = formState.qslSent,
                        onCheckedChange = { viewModel.updateFormField { copy(qslSent = it) } }
                    )
                    Text(stringResource(R.string.qso_qsl_sent))
                }
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(
                        checked = formState.qslReceived,
                        onCheckedChange = { viewModel.updateFormField { copy(qslReceived = it) } }
                    )
                    Text(stringResource(R.string.qso_qsl_received))
                }
            }

            HorizontalDivider()

            // Notes
            OutlinedTextField(
                value = formState.notes,
                onValueChange = { viewModel.updateFormField { copy(notes = it) } },
                label = { Text(stringResource(R.string.qso_notes)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Duplicate warning dialog
        if (uiState.duplicateWarning != null) {
            AlertDialog(
                onDismissRequest = viewModel::dismissDuplicateWarning,
                title = { Text("Possible Duplicate") },
                text = {
                    Text(
                        "A QSO with ${uiState.duplicateWarning?.callsign} on ${uiState.duplicateWarning?.band} " +
                        "${uiState.duplicateWarning?.mode} already exists for ${uiState.duplicateWarning?.date}.\n\n" +
                        "Do you want to save anyway?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                viewModel.saveQsoIgnoringDuplicate(qsoId)
                                onSaved()
                            }
                        }
                    ) {
                        Text("Save Anyway")
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::dismissDuplicateWarning) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BandDropdown(
    selectedBand: String,
    onBandSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedBand,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.qso_band) + " *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Band.entries.forEach { band ->
                DropdownMenuItem(
                    text = { Text("${band.display} (${band.frequencyRange})") },
                    onClick = {
                        onBandSelected(band.display)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModeDropdown(
    selectedMode: String,
    onModeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedMode,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.qso_mode) + " *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Mode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.display) },
                    onClick = {
                        onModeSelected(mode.display)
                        expanded = false
                    }
                )
            }
        }
    }
}

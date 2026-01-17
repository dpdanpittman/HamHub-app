package com.hamhub.app.ui.screens.logbook

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hamhub.app.R
import com.hamhub.app.domain.model.Band
import com.hamhub.app.domain.model.Mode
import com.hamhub.app.ui.components.QsoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogbookScreen(
    onAddQso: () -> Unit,
    onEditQso: (Long) -> Unit,
    viewModel: LogbookViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_logbook)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Badge(
                            modifier = Modifier.offset(x = 8.dp, y = (-8).dp),
                            containerColor = if (uiState.selectedBandFilter != null || uiState.selectedModeFilter != null)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primaryContainer
                        ) {
                            if (uiState.selectedBandFilter != null || uiState.selectedModeFilter != null) {
                                val count = listOfNotNull(uiState.selectedBandFilter, uiState.selectedModeFilter).size
                                Text(count.toString())
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = stringResource(R.string.action_filter)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.initNewQso()
                    onAddQso()
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.action_add_qso)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.action_search)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true
            )

            // Active filters display
            if (uiState.selectedBandFilter != null || uiState.selectedModeFilter != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    uiState.selectedBandFilter?.let { band ->
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.onBandFilterChange(null) },
                            label = { Text(band) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Remove filter",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                    uiState.selectedModeFilter?.let { mode ->
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.onModeFilterChange(null) },
                            label = { Text(mode) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Remove filter",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                    TextButton(onClick = viewModel::clearFilters) {
                        Text("Clear all")
                    }
                }
            }

            // QSO count
            Text(
                text = "${uiState.qsos.size} QSOs",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // QSO list
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.qsos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.searchQuery.isNotEmpty() || uiState.selectedBandFilter != null || uiState.selectedModeFilter != null)
                            "No QSOs match your filters"
                        else
                            stringResource(R.string.msg_no_qsos),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.qsos,
                        key = { it.id }
                    ) { qso ->
                        QsoCard(
                            qso = qso,
                            onClick = { onEditQso(qso.id) },
                            onEdit = { onEditQso(qso.id) },
                            onDelete = { viewModel.showDeleteConfirmation(qso) }
                        )
                    }
                }
            }
        }

        // Delete confirmation dialog
        if (uiState.showDeleteConfirmation && uiState.qsoToDelete != null) {
            AlertDialog(
                onDismissRequest = viewModel::dismissDeleteConfirmation,
                title = { Text(stringResource(R.string.confirm_delete_title)) },
                text = {
                    Text(
                        "Delete QSO with ${uiState.qsoToDelete?.callsign}?\n\n" +
                        stringResource(R.string.confirm_delete_message)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = viewModel::deleteQso,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(R.string.action_delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::dismissDeleteConfirmation) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            )
        }

        // Filter bottom sheet
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false }
            ) {
                FilterSheetContent(
                    selectedBand = uiState.selectedBandFilter,
                    selectedMode = uiState.selectedModeFilter,
                    onBandSelected = { band ->
                        viewModel.onBandFilterChange(band)
                    },
                    onModeSelected = { mode ->
                        viewModel.onModeFilterChange(mode)
                    },
                    onApply = { showFilterSheet = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSheetContent(
    selectedBand: String?,
    selectedMode: String?,
    onBandSelected: (String?) -> Unit,
    onModeSelected: (String?) -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Filter by Band",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Band.entries.forEach { band ->
                FilterChip(
                    selected = selectedBand == band.display,
                    onClick = {
                        onBandSelected(if (selectedBand == band.display) null else band.display)
                    },
                    label = { Text(band.display) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Filter by Mode",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Mode.entries.forEach { mode ->
                FilterChip(
                    selected = selectedMode == mode.display,
                    onClick = {
                        onModeSelected(if (selectedMode == mode.display) null else mode.display)
                    },
                    label = { Text(mode.display) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onApply,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apply Filters")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

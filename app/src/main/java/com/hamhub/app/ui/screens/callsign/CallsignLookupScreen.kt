package com.hamhub.app.ui.screens.callsign

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hamhub.app.R
import com.hamhub.app.data.remote.dto.CallookResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallsignLookupScreen(
    onBack: () -> Unit,
    viewModel: CallsignViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_callsign)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        ) {
            // Search section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Callsign Lookup",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Search FCC database via Callook.info",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uiState.query,
                            onValueChange = { viewModel.updateQuery(it) },
                            modifier = Modifier.weight(1f),
                            label = { Text("Callsign") },
                            placeholder = { Text("e.g., W1AW") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            trailingIcon = {
                                if (uiState.query.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.clear() }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    keyboardController?.hide()
                                    viewModel.search()
                                }
                            )
                        )

                        Button(
                            onClick = {
                                keyboardController?.hide()
                                viewModel.search()
                            },
                            enabled = uiState.query.isNotBlank() && !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Search")
                            }
                        }
                    }
                }
            }

            // Results section
            when {
                uiState.error != null -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = uiState.error!!,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                uiState.result != null -> {
                    CallsignResultCard(
                        result = uiState.result!!,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }

                !uiState.hasSearched -> {
                    // Initial state - show help text
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.PersonSearch,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Enter a US callsign to look up license information from the FCC database.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CallsignResultCard(
    result: CallookResponse,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Callsign header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Badge,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = result.current?.callsign ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${result.type} - ${result.current?.operClass ?: ""} Class",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Name
            if (result.name.isNotBlank()) {
                InfoRow(
                    icon = Icons.Default.Person,
                    label = "Name",
                    value = result.name
                )
            }

            // Address
            result.address?.let { address ->
                val addressText = buildString {
                    if (address.line1.isNotBlank()) append(address.line1)
                    if (address.line2.isNotBlank()) {
                        if (isNotEmpty()) append("\n")
                        append(address.line2)
                    }
                }
                if (addressText.isNotBlank()) {
                    InfoRow(
                        icon = Icons.Default.LocationOn,
                        label = "Address",
                        value = addressText
                    )
                }
            }

            // Location
            result.location?.let { location ->
                if (location.gridsquare.isNotBlank()) {
                    InfoRow(
                        icon = Icons.Default.GridOn,
                        label = "Grid Square",
                        value = location.gridsquare
                    )
                }
                if (location.latitude.isNotBlank() && location.longitude.isNotBlank()) {
                    InfoRow(
                        icon = Icons.Default.MyLocation,
                        label = "Coordinates",
                        value = "${location.latitude}, ${location.longitude}"
                    )
                }
            }

            // Previous callsign
            result.previous?.let { previous ->
                if (previous.callsign.isNotBlank()) {
                    InfoRow(
                        icon = Icons.Default.History,
                        label = "Previous Callsign",
                        value = previous.callsign
                    )
                }
            }

            // License info
            result.otherInfo?.let { info ->
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text(
                    text = "License Information",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (info.grantDate.isNotBlank()) {
                    InfoRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Grant Date",
                        value = info.grantDate
                    )
                }
                if (info.expiryDate.isNotBlank()) {
                    InfoRow(
                        icon = Icons.Default.Event,
                        label = "Expiry Date",
                        value = info.expiryDate
                    )
                }
                if (info.frn.isNotBlank()) {
                    InfoRow(
                        icon = Icons.Default.Numbers,
                        label = "FRN",
                        value = info.frn
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

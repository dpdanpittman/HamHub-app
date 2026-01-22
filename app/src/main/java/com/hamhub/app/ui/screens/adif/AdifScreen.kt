package com.hamhub.app.ui.screens.adif

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.hamhub.app.R
import com.hamhub.app.domain.model.Band
import com.hamhub.app.domain.model.Mode
import com.hamhub.app.ui.components.CompactHeader
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdifScreen(
    onBack: () -> Unit,
    viewModel: AdifViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // File picker for import
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importFromUri(context, it) }
    }

    // File creator for export
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        uri?.let { outputUri ->
            scope.launch {
                try {
                    val result = viewModel.getExportContent()
                    context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                        outputStream.write(result.content.toByteArray())
                    }
                } catch (e: Exception) {
                    // Error handling
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CompactHeader(
                title = "Import / Export",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Import Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.FileUpload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.action_import),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Import QSOs from an ADIF file (.adi, .adif). Duplicate contacts will be skipped.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = {
                            importLauncher.launch(arrayOf("*/*"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isImporting
                    ) {
                        if (uiState.isImporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Select ADIF File")
                    }
                }
            }

            // Export Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.action_export),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Export your logbook to ADIF format for backup or use in other logging software.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Filter options
                    Text(
                        text = "Filter (optional)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Band filter chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilterChip(
                            selected = uiState.bandFilter == null,
                            onClick = { viewModel.setBandFilter(null) },
                            label = { Text("All Bands") }
                        )
                        Band.entries.take(6).forEach { band ->
                            FilterChip(
                                selected = uiState.bandFilter == band.display,
                                onClick = {
                                    viewModel.setBandFilter(
                                        if (uiState.bandFilter == band.display) null else band.display
                                    )
                                },
                                label = { Text(band.display) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Save to file
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val result = viewModel.getExportContent()
                                    exportLauncher.launch(result.filename)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.isExporting
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save File")
                        }

                        // Share
                        Button(
                            onClick = {
                                scope.launch {
                                    val result = viewModel.getExportContent()
                                    shareAdifContent(context, result.content, result.filename)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.isExporting
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share")
                        }
                    }
                }
            }

            // About ADIF
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "About ADIF",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ADIF (Amateur Data Interchange Format) is a standard file format for exchanging amateur radio log data between different logging programs. HamHub supports ADIF version 3.1.4.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Import result dialog
        uiState.importResult?.let { result ->
            AlertDialog(
                onDismissRequest = viewModel::dismissResult,
                title = { Text("Import Complete") },
                text = {
                    Column {
                        Text("Total records: ${result.totalRecords}")
                        Text("Imported: ${result.importedRecords}")
                        if (result.duplicatesSkipped > 0) {
                            Text("Duplicates skipped: ${result.duplicatesSkipped}")
                        }
                        if (result.errors.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Errors: ${result.errors.size}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = viewModel::dismissResult) {
                        Text("OK")
                    }
                }
            )
        }

        // Export result dialog
        uiState.exportResult?.let { result ->
            AlertDialog(
                onDismissRequest = viewModel::dismissResult,
                title = { Text("Export Complete") },
                text = {
                    Text("Exported ${result.qsoCount} QSOs to ${result.filename}")
                },
                confirmButton = {
                    TextButton(onClick = viewModel::dismissResult) {
                        Text("OK")
                    }
                }
            )
        }

        // Error dialog
        uiState.error?.let { error ->
            AlertDialog(
                onDismissRequest = viewModel::dismissResult,
                title = { Text("Error") },
                text = { Text(error) },
                confirmButton = {
                    TextButton(onClick = viewModel::dismissResult) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

private fun shareAdifContent(context: android.content.Context, content: String, filename: String) {
    try {
        // Write to cache file
        val cacheDir = File(context.cacheDir, "exports")
        cacheDir.mkdirs()
        val file = File(cacheDir, filename)
        file.writeText(content)

        // Get content URI using FileProvider
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        // Create share intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share ADIF Export"))
    } catch (e: Exception) {
        // Handle error silently or show toast
    }
}

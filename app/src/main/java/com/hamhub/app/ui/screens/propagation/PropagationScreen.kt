package com.hamhub.app.ui.screens.propagation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hamhub.app.R
import com.hamhub.app.data.remote.dto.PropagationData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropagationScreen(
    onBack: () -> Unit,
    viewModel: PropagationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_propagation)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Unable to load data",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = uiState.error!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Retry")
                        }
                    }
                }
                uiState.data != null -> {
                    PropagationContent(data = uiState.data!!)
                }
            }
        }
    }
}

@Composable
private fun PropagationContent(data: PropagationData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Solar indices card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Solar Conditions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SolarIndex("SFI", data.solarFluxIndex.toString(), getSfiColor(data.solarFluxIndex))
                    SolarIndex("SSN", data.sunspotNumber.toString(), getSsnColor(data.sunspotNumber))
                    SolarIndex("A", data.aIndex.toString(), getAIndexColor(data.aIndex))
                    SolarIndex("K", data.kIndex.toString(), getKIndexColor(data.kIndex))
                }

                if (data.updated.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Updated: ${data.updated}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Geomagnetic conditions
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Geomagnetic Conditions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                ConditionRow("X-Ray Flare", data.xrayFlare)
                ConditionRow("Geomag Field", data.geomagField)
                ConditionRow("Signal Noise", data.signalNoise)
            }
        }

        // Band conditions
        if (data.bandConditions.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "HF Band Conditions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Band",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Day",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Night",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    data.bandConditions.forEach { (band, condition) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = band,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            ConditionBadge(
                                condition = condition.dayCondition,
                                modifier = Modifier.weight(1f)
                            )
                            ConditionBadge(
                                condition = condition.nightCondition,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Legend
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
                    text = "Index Guide",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "SFI (Solar Flux): Higher is better for HF (>100 good)\n" +
                           "SSN (Sunspot Number): Higher means more activity\n" +
                           "A-Index: Lower is better (<10 quiet)\n" +
                           "K-Index: Lower is better (0-2 quiet, 5+ storm)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SolarIndex(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun ConditionRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value.ifEmpty { "N/A" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ConditionBadge(condition: String, modifier: Modifier = Modifier) {
    val color = when (condition.lowercase()) {
        "good" -> Color(0xFF4CAF50)
        "fair" -> Color(0xFFFFC107)
        "poor" -> Color(0xFFFF5722)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = condition,
        style = MaterialTheme.typography.bodySmall,
        color = color,
        fontWeight = FontWeight.Medium,
        modifier = modifier
    )
}

private fun getSfiColor(sfi: Int): Color = when {
    sfi >= 150 -> Color(0xFF4CAF50)
    sfi >= 100 -> Color(0xFF8BC34A)
    sfi >= 70 -> Color(0xFFFFC107)
    else -> Color(0xFFFF5722)
}

private fun getSsnColor(ssn: Int): Color = when {
    ssn >= 100 -> Color(0xFF4CAF50)
    ssn >= 50 -> Color(0xFF8BC34A)
    ssn >= 20 -> Color(0xFFFFC107)
    else -> Color(0xFFFF5722)
}

private fun getAIndexColor(a: Int): Color = when {
    a < 10 -> Color(0xFF4CAF50)
    a < 20 -> Color(0xFF8BC34A)
    a < 50 -> Color(0xFFFFC107)
    else -> Color(0xFFFF5722)
}

private fun getKIndexColor(k: Int): Color = when {
    k <= 2 -> Color(0xFF4CAF50)
    k <= 3 -> Color(0xFF8BC34A)
    k <= 4 -> Color(0xFFFFC107)
    else -> Color(0xFFFF5722)
}

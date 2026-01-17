package com.hamhub.app.ui.screens.bandplan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hamhub.app.domain.model.BandAllocation
import com.hamhub.app.domain.model.BandPlan
import com.hamhub.app.domain.model.BandPlanData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandDetailScreen(
    band: String,
    onBack: () -> Unit
) {
    val bandPlan = remember(band) {
        BandPlanData.allBands.find { it.name == band }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$band Band") },
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
        if (bandPlan == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Band not found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Band overview card
                item {
                    BandOverviewCard(band = bandPlan)
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Frequency Allocations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Allocation items
                items(bandPlan.allocations) { allocation ->
                    AllocationCard(allocation = allocation)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Note",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "This band plan is based on ARRL recommendations for US amateur radio operators. " +
                                       "Always check current FCC regulations for the most up-to-date information.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BandOverviewCard(band: BandPlan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${band.name} Band",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Start Frequency",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "%.3f MHz".format(band.startFreq),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "End Frequency",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "%.3f MHz".format(band.endFreq),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val bandwidth = band.endFreq - band.startFreq
            Text(
                text = "Bandwidth: %.3f MHz".format(bandwidth),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun AllocationCard(allocation: BandAllocation) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "%.3f - %.3f MHz".format(allocation.startFreq, allocation.endFreq),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Modes
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                allocation.modes.forEach { mode ->
                    Surface(
                        color = getModeColor(mode),
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = mode,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }

            // Notes
            if (allocation.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = allocation.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun getModeColor(mode: String): Color {
    return when (mode.uppercase()) {
        "CW" -> Color(0xFF2196F3)
        "SSB", "PHONE" -> Color(0xFF4CAF50)
        "FM" -> Color(0xFFFF9800)
        "RTTY", "DATA" -> Color(0xFF9C27B0)
        "AM" -> Color(0xFF795548)
        "DIGITAL" -> Color(0xFF00BCD4)
        "PACKET" -> Color(0xFF607D8B)
        "ATV", "IMAGE" -> Color(0xFFE91E63)
        "SATELLITE" -> Color(0xFF3F51B5)
        "EME" -> Color(0xFF673AB7)
        "WEAK SIGNAL" -> Color(0xFF009688)
        "BEACONS" -> Color(0xFFCDDC39)
        "REPEATER" -> Color(0xFFFF5722)
        "ALL MODES" -> Color(0xFF757575)
        else -> Color(0xFF9E9E9E)
    }
}

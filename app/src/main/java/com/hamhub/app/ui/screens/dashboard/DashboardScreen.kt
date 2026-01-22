package com.hamhub.app.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hamhub.app.R
import com.hamhub.app.ui.components.*
import com.hamhub.app.ui.components.CompactHeader

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val bandColors = listOf(
        Color(0xFFE57373), // Red
        Color(0xFFFFB74D), // Orange
        Color(0xFFFFD54F), // Yellow
        Color(0xFF81C784), // Green
        Color(0xFF4FC3F7), // Light Blue
        Color(0xFF7986CB), // Indigo
        Color(0xFFBA68C8), // Purple
        Color(0xFF4DB6AC), // Teal
        Color(0xFFA1887F), // Brown
        Color(0xFF90A4AE)  // Blue Grey
    )

    val modeColors = listOf(
        Color(0xFF42A5F5), // Blue
        Color(0xFF66BB6A), // Green
        Color(0xFFFFA726), // Orange
        Color(0xFFAB47BC), // Purple
        Color(0xFFEC407A), // Pink
        Color(0xFF26A69A)  // Teal
    )

    Scaffold(
        topBar = {
            CompactHeader(
                title = stringResource(R.string.nav_dashboard)
            )
        }
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stats cards - Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total QSOs",
                        value = uiState.stats.totalQsos.toString(),
                        icon = Icons.Default.Radio,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Callsigns",
                        value = uiState.stats.uniqueCallsigns.toString(),
                        icon = Icons.Default.Person,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Stats cards - Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Countries",
                        value = uiState.stats.uniqueCountries.toString(),
                        icon = Icons.Default.Public,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Grids",
                        value = uiState.stats.uniqueGrids.toString(),
                        icon = Icons.Default.GridOn,
                        modifier = Modifier.weight(1f)
                    )
                }

                // QSOs by Band chart
                if (uiState.stats.qsosByBand.isNotEmpty()) {
                    ChartCard(title = "QSOs by Band") {
                        HorizontalBarChart(
                            data = uiState.stats.qsosByBand,
                            colors = bandColors
                        )
                    }
                }

                // QSOs by Mode chart
                if (uiState.stats.qsosByMode.isNotEmpty()) {
                    ChartCard(title = "QSOs by Mode") {
                        HorizontalBarChart(
                            data = uiState.stats.qsosByMode,
                            colors = modeColors
                        )
                    }
                }

                // QSOs over time chart
                if (uiState.stats.qsosByMonth.isNotEmpty()) {
                    ChartCard(title = "QSOs Over Time") {
                        SimpleLineChart(
                            data = uiState.stats.qsosByMonth,
                            lineColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // QSOs by Hour chart
                if (uiState.stats.qsosByHour.isNotEmpty()) {
                    ChartCard(title = "Activity by Hour (UTC)") {
                        HourDistributionChart(
                            data = uiState.stats.qsosByHour,
                            barColor = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                // Recent QSOs
                if (uiState.stats.recentQsos.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Recent Contacts",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            uiState.stats.recentQsos.forEach { qso ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = qso.callsign,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "${qso.band} ${qso.mode}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "${qso.date}\n${qso.timeUtc}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (qso != uiState.stats.recentQsos.last()) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Empty state
                if (uiState.stats.totalQsos == 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radio,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No QSOs yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Start logging contacts to see your statistics here",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ChartCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

package com.hamhub.app.ui.screens.bandplan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hamhub.app.R
import com.hamhub.app.domain.model.BandPlan
import com.hamhub.app.domain.model.BandPlanData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandPlanScreen(
    onBandClick: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("HF Bands", "VHF/UHF")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_bandplan)) },
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
        ) {
            // Tab row
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Band list
            val bands = when (selectedTab) {
                0 -> BandPlanData.hfBands
                else -> BandPlanData.vhfUhfBands
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bands) { band ->
                    BandCard(
                        band = band,
                        onClick = { onBandClick(band.name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BandCard(
    band: BandPlan,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = getBandColor(band.name),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = band.name,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Column {
                    Text(
                        text = "${band.name} Band",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "%.3f - %.3f MHz".format(band.startFreq, band.endFreq),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun getBandColor(band: String): Color {
    return when (band) {
        "160m" -> Color(0xFF5D4037)
        "80m" -> Color(0xFF7B1FA2)
        "60m" -> Color(0xFF512DA8)
        "40m" -> Color(0xFF303F9F)
        "30m" -> Color(0xFF1976D2)
        "20m" -> Color(0xFF0288D1)
        "17m" -> Color(0xFF0097A7)
        "15m" -> Color(0xFF00796B)
        "12m" -> Color(0xFF388E3C)
        "10m" -> Color(0xFF689F38)
        "6m" -> Color(0xFFAFB42B)
        "2m" -> Color(0xFFFBC02D)
        "1.25m" -> Color(0xFFF57C00)
        "70cm" -> Color(0xFFE64A19)
        "33cm" -> Color(0xFFD32F2F)
        "23cm" -> Color(0xFFC2185B)
        else -> Color(0xFF757575)
    }
}

package com.hamhub.app.ui.screens.iss

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.hamhub.app.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssTrackerScreen(
    onBack: () -> Unit,
    viewModel: IssViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_iss)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.apiKeyConfigured) {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
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
                !uiState.apiKeyConfigured -> {
                    ApiKeySetupScreen(
                        onSaveApiKey = { viewModel.saveApiKey(it) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.isLoading && uiState.lastUpdate == 0L -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null && uiState.lastUpdate == 0L -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Unable to track ISS", style = MaterialTheme.typography.titleMedium)
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
                else -> {
                    // Map
                    IssMapView(
                        latitude = uiState.latitude,
                        longitude = uiState.longitude,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Info overlay
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Satellite,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = uiState.satName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column {
                                    Text(
                                        text = "Latitude",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "%.4f°".format(uiState.latitude),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Longitude",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "%.4f°".format(uiState.longitude),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            if (uiState.altitude > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Altitude: %.1f km".format(uiState.altitude),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.GridOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Grid: ${uiState.gridSquare}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            if (uiState.lastUpdate > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                                dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                                Text(
                                    text = "Updated: ${dateFormat.format(Date(uiState.lastUpdate * 1000))} UTC",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Ham radio info card
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "ISS Ham Radio",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Callsigns: NA1SS, RS0ISS\n" +
                                       "Voice: 145.800 MHz (downlink)\n" +
                                       "APRS: 145.825 MHz",
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
private fun ApiKeySetupScreen(
    onSaveApiKey: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var apiKey by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            Icons.Default.Satellite,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "ISS Tracker Setup",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "To track the International Space Station, you need a free API key from N2YO.com",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "How to get your API key:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "1. Visit n2yo.com and create a free account\n" +
                           "2. Go to your profile and find the API section\n" +
                           "3. Copy your API key and paste it below",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        OutlinedButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.n2yo.com/api/"))
                context.startActivity(intent)
            }
        ) {
            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Get API Key at N2YO.com")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("N2YO API Key") },
            placeholder = { Text("Paste your API key here") },
            leadingIcon = {
                Icon(Icons.Default.Key, contentDescription = null)
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Button(
            onClick = { onSaveApiKey(apiKey) },
            modifier = Modifier.fillMaxWidth(),
            enabled = apiKey.isNotBlank()
        ) {
            Text("Save API Key & Start Tracking")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "The free tier allows 1000 API calls per hour, which is plenty for tracking.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun IssMapView(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(4.0)
                zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            // Add ISS marker
            val issMarker = Marker(mapView).apply {
                position = GeoPoint(latitude, longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                title = "ISS"
                snippet = "International Space Station"
            }
            mapView.overlays.add(issMarker)

            // Center on ISS
            mapView.controller.animateTo(GeoPoint(latitude, longitude))

            mapView.invalidate()
        },
        modifier = modifier
    )
}

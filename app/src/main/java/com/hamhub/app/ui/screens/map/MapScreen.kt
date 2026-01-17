package com.hamhub.app.ui.screens.map

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.hamhub.app.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Initialize osmdroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_map)) },
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
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Map view
                OsmdroidMapView(
                    markers = uiState.clusteredMarkers,
                    onMarkerClick = { marker -> viewModel.selectMarker(marker) },
                    modifier = Modifier.fillMaxSize()
                )

                // Stats overlay
                Card(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.totalQsosWithGrid.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "QSOs",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.uniqueGrids.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Grids",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                // Empty state overlay
                if (uiState.markers.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No locations to display",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Add grid squares to your QSOs to see them on the map",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Marker details bottom sheet
                uiState.selectedMarker?.let { marker ->
                    MarkerDetailsSheet(
                        marker = marker,
                        onDismiss = { viewModel.dismissMarkerDetails() },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@Composable
private fun OsmdroidMapView(
    markers: List<MapMarker>,
    onMarkerClick: (MapMarker) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                // Set initial view to show the world
                controller.setZoom(3.0)
                controller.setCenter(GeoPoint(20.0, 0.0))

                // Enable zoom controls
                zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
            }
        },
        update = { mapView ->
            // Clear existing markers
            mapView.overlays.clear()

            // Add markers
            markers.forEach { markerData ->
                val marker = Marker(mapView).apply {
                    position = GeoPoint(markerData.latitude, markerData.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = markerData.callsign
                    snippet = "${markerData.gridSquare} - ${markerData.band} ${markerData.mode}"

                    // Use different icon for clusters
                    if (markerData.qsoCount > 1) {
                        // Cluster marker
                        try {
                            icon = ContextCompat.getDrawable(context, android.R.drawable.ic_dialog_map)
                        } catch (e: Exception) {
                            // Use default
                        }
                    }

                    setOnMarkerClickListener { _, _ ->
                        onMarkerClick(markerData)
                        true
                    }
                }
                mapView.overlays.add(marker)
            }

            // Refresh the map
            mapView.invalidate()

            // If we have markers, zoom to fit them
            if (markers.isNotEmpty()) {
                val boundingBox = org.osmdroid.util.BoundingBox.fromGeoPoints(
                    markers.map { GeoPoint(it.latitude, it.longitude) }
                )
                mapView.post {
                    try {
                        mapView.zoomToBoundingBox(boundingBox, true, 50)
                    } catch (e: Exception) {
                        // Fallback if zoom fails
                        mapView.controller.setZoom(4.0)
                    }
                }
            }
        },
        modifier = modifier
    )
}

@Composable
private fun MarkerDetailsSheet(
    marker: MapMarker,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = marker.callsign,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text(marker.gridSquare) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.GridOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
                AssistChip(
                    onClick = { },
                    label = { Text(marker.band) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Radio,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
                AssistChip(
                    onClick = { },
                    label = { Text(marker.mode) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Date: ${marker.date}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (marker.qsoCount > 1) {
                    Text(
                        text = "${marker.qsoCount} QSOs in this grid",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Lat: ${"%.4f".format(marker.latitude)}, Lon: ${"%.4f".format(marker.longitude)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

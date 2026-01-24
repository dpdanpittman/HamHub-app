package com.hamhub.app.ui.screens.more

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hamhub.app.R
import com.hamhub.app.ui.navigation.Screen

@Composable
fun MoreScreen(
    onNavigate: (Screen) -> Unit
) {
    // Organize menu items into sections
    val sections = listOf(
        MenuSection(
            title = "Learn",
            items = listOf(Screen.Guide, Screen.Resources, Screen.Calculators, Screen.BandPlan, Screen.OtherServices)
        ),
        MenuSection(
            title = "Live Data",
            items = listOf(Screen.Propagation, Screen.IssTracker, Screen.Repeaters, Screen.CallsignLookup, Screen.Spotter)
        ),
        MenuSection(
            title = "Community",
            items = listOf(Screen.Contests, Screen.News)
        ),
        MenuSection(
            title = "Tools",
            items = listOf(Screen.ImportExport, Screen.Settings)
        )
    )

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HamHub",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_header_logo),
                        contentDescription = "HamHub Logo",
                        modifier = Modifier.size(28.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Welcome intro
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Welcome to HamHub",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your complete amateur radio companion. Log contacts, track awards, explore propagation conditions, find repeaters, and access essential ham radio references - all in one app.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Menu sections
            sections.forEach { section ->
                item {
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(section.items) { screen ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(screen) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = getScreenDescription(screen),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

private data class MenuSection(
    val title: String,
    val items: List<Screen>
)

private fun getScreenDescription(screen: Screen): String {
    return when (screen) {
        Screen.Guide -> "Learn how to log QSOs and use the app"
        Screen.Resources -> "Q-codes, Morse code, RST, phonetics"
        Screen.Calculators -> "Antenna and electrical calculators"
        Screen.BandPlan -> "HF, VHF, UHF frequency allocations"
        Screen.OtherServices -> "CB, FRS, GMRS, MURS, Marine VHF"
        Screen.Propagation -> "Solar conditions and band openings"
        Screen.IssTracker -> "Track the International Space Station"
        Screen.Repeaters -> "Find nearby repeaters"
        Screen.CallsignLookup -> "Look up amateur callsigns"
        Screen.Spotter -> "Save and organize spotted callsigns"
        Screen.Contests -> "Upcoming amateur radio contests"
        Screen.News -> "ARRL news and DX bulletins"
        Screen.ImportExport -> "Import/export ADIF log files"
        Screen.Settings -> "App preferences and your callsign"
        else -> ""
    }
}

package com.hamhub.app.ui.screens.guide

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hamhub.app.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GuideScreen(
    onBack: () -> Unit
) {
    val tabs = listOf("Basics", "SWL", "Fields", "Tips")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_guide)) },
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
            TabRow(
                selectedTabIndex = pagerState.currentPage
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> LoggingBasicsTab()
                    1 -> SwlGuideTab()
                    2 -> FieldGuideTab()
                    3 -> TipsTab()
                }
            }
        }
    }
}

@Composable
private fun LoggingBasicsTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "What is a QSO?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A QSO is a two-way radio contact between amateur radio operators. When you make a contact, you log the details so you can track your activity, apply for awards, and confirm contacts with other stations.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Text(
                text = "Minimum Information to Log",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "At minimum, every QSO should include these required fields:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                BulletPoint("Date & Time (UTC) - When the contact happened")
                BulletPoint("Callsign - The other station's callsign")
                BulletPoint("Band - Which frequency band you used (e.g., 20m, 40m)")
                BulletPoint("Mode - How you communicated (SSB, CW, FT8, etc.)")
            }
        }

        item {
            Text(
                text = "Typical QSO Exchange",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QsoExchangeLine("You:", "CQ CQ CQ, this is W1ABC, Whiskey One Alpha Bravo Charlie", true)
                    QsoExchangeLine("Them:", "W1ABC, this is K2XYZ, you're 59 in New York", false)
                    QsoExchangeLine("You:", "K2XYZ, thanks! You're also 59 here in Boston. Name is John.", true)
                    QsoExchangeLine("Them:", "Thanks John, I'm Mike. 73!", false)
                    QsoExchangeLine("You:", "73, K2XYZ. W1ABC clear.", true)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "From this exchange, you'd log: Callsign (K2XYZ), RST Sent (59), RST Received (59), Name (Mike), QTH (New York).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Text(
                text = "Why Use UTC Time?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ham radio uses UTC (Coordinated Universal Time) so operators worldwide can reference the same time regardless of time zones. Your device's current UTC time is shown when you open the QSO form.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            TipCard("Many hams set a clock in their shack to UTC time to make logging easier.")
        }
    }
}

@Composable
private fun SwlGuideTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "What is SWL?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SWL stands for Short Wave Listening. If you're just listening to ham radio without transmitting (maybe you don't have a license yet, or you're learning), you can still log what you hear!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Text(
                text = "How to Log Receive-Only Contacts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "When you're only receiving, here's what to fill in:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    SwlFieldRow("Callsign", "The station you heard")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SwlFieldRow("Date/Time", "When you heard them (UTC)")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SwlFieldRow("Band & Frequency", "Where you heard them")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SwlFieldRow("Mode", "SSB, CW, FT8, etc.")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SwlFieldRow("RST Sent", "Leave blank or enter \"000\"")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SwlFieldRow("RST Received", "Your assessment of their signal")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SwlFieldRow("Notes", "Add \"SWL\" or \"Receive only\" to mark it")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Important Note",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "SWL logs are for your personal records and learning. They don't count toward awards like DXCC or WAS, which require two-way contacts. However, they're great for practicing your logging skills!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = "SWL Tips",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                BulletPoint("Listen to CQ calls to learn proper procedures")
                BulletPoint("FT8 on 14.074 MHz is great for seeing callsigns")
                BulletPoint("Try 20m (14 MHz) during the day for worldwide stations")
                BulletPoint("Try 40m (7 MHz) in the evening for regional contacts")
                BulletPoint("Log everything - it helps you learn band conditions")
            }
        }
    }
}

@Composable
private fun FieldGuideTab() {
    val fields = listOf(
        FieldInfo("Date & Time (UTC)", true, "The date and time of your contact in Coordinated Universal Time.", "2024-03-15 14:30 UTC"),
        FieldInfo("Callsign", true, "The unique identifier of the station you contacted.", "W1ABC, VE3XYZ, JA1ABC"),
        FieldInfo("Band", true, "The amateur radio band used for the contact.", "20m = worldwide, 40m = regional"),
        FieldInfo("Mode", true, "The transmission mode used.", "SSB (voice), CW (Morse), FT8 (digital)"),
        FieldInfo("Frequency", false, "The exact frequency in MHz.", "14.250 MHz, 7.185 MHz"),
        FieldInfo("RST Sent / Received", false, "Signal reports: Readability (1-5), Strength (1-9), Tone (1-9, CW only).", "59 = Loud and clear"),
        FieldInfo("Name", false, "The operator's first name.", "John, Mike"),
        FieldInfo("QTH", false, "Their location (city, town, or area).", "Boston, MA"),
        FieldInfo("Grid Square", false, "Maidenhead grid locator (4 or 6 characters).", "FN42, IO91"),
        FieldInfo("Country", false, "The DXCC entity for award tracking.", "United States, Japan"),
        FieldInfo("State/Province", false, "US state or Canadian province for WAS award.", "CA, TX, ON"),
        FieldInfo("Power", false, "Your transmit power in watts.", "100W, 5W (QRP)"),
        FieldInfo("QSL Sent/Received", false, "Track confirmation cards.", "Y = Yes, N = No"),
        FieldInfo("Notes", false, "Any additional information.", "Contest, SWL, antenna used")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Fields marked with * are required for a valid QSO log.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(fields) { field ->
            FieldCard(field)
        }
    }
}

@Composable
private fun TipsTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Logging Best Practices",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                BulletPoint("Log immediately - Don't wait until after your session")
                BulletPoint("Double-check callsigns - Wrong callsign = no confirmation")
                BulletPoint("Use UTC consistently - All times should be in UTC")
                BulletPoint("Be honest with RST - Log what you actually heard")
                BulletPoint("Add notes for memorable contacts")
            }
        }

        item {
            Text(
                text = "Common Mistakes to Avoid",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DosDontsRow("Log contacts in local time", "Always use UTC time")
                DosDontsRow("Guess at callsigns you didn't copy", "Ask for repeats until you're sure")
                DosDontsRow("Forget to log frequency for FT8", "Log exact frequency for QSO matching")
            }
        }

        item {
            Text(
                text = "Award Tracking Tips",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "If you're working toward awards, make sure to log:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                BulletPoint("For DXCC: Always enter the country name")
                BulletPoint("For WAS: Enter the US state code (2 letters)")
                BulletPoint("For Grid squares: Get the grid from the other station")
            }
        }

        item {
            Text(
                text = "Exporting Your Log",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You can export your log in ADIF format from the Import/Export screen. ADIF (Amateur Data Interchange Format) is the standard format used by LoTW, eQSL, QRZ, and other services.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Helper composables

@Composable
private fun BulletPoint(text: String) {
    Row {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QsoExchangeLine(speaker: String, text: String, isYou: Boolean) {
    Column {
        Text(
            text = speaker,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isYou) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun TipCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tip:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SwlFieldRow(field: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = field,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.6f)
        )
    }
}

private data class FieldInfo(
    val name: String,
    val required: Boolean,
    val description: String,
    val example: String
)

@Composable
private fun FieldCard(field: FieldInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = field.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                if (field.required) {
                    Text(
                        text = "*",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = field.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Example: ${field.example}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DosDontsRow(dont: String, doThis: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Don't",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = dont,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Do",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = doThis,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

package com.hamhub.app.ui.screens.calculators

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hamhub.app.R
import com.hamhub.app.domain.util.AntennaCalculations
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CalculatorsScreen(
    onBack: () -> Unit
) {
    val tabs = listOf("Antenna", "Electrical", "Power", "SWR")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_calculators)) },
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
                    0 -> AntennaCalculatorTab()
                    1 -> ElectricalCalculatorTab()
                    2 -> PowerCalculatorTab()
                    3 -> SwrCalculatorTab()
                }
            }
        }
    }
}

@Composable
private fun AntennaCalculatorTab() {
    var frequency by remember { mutableStateOf("14.200") }
    val freqValue = frequency.toDoubleOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Frequency input
        OutlinedTextField(
            value = frequency,
            onValueChange = { frequency = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Frequency (MHz)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        if (freqValue > 0) {
            // Dipole
            CalculatorResultCard(
                title = "Half-Wave Dipole",
                description = "Total length for a center-fed dipole antenna",
                results = listOf(
                    "Length" to "%.2f ft (%.2f m)".format(
                        AntennaCalculations.dipoleLengthFeet(freqValue),
                        AntennaCalculations.dipoleLengthMeters(freqValue)
                    ),
                    "Each leg" to "%.2f ft (%.2f m)".format(
                        AntennaCalculations.dipoleLengthFeet(freqValue) / 2,
                        AntennaCalculations.dipoleLengthMeters(freqValue) / 2
                    )
                ),
                formula = "468 / freq (MHz)"
            )

            // Quarter-wave vertical
            CalculatorResultCard(
                title = "Quarter-Wave Vertical",
                description = "Vertical radiator length (requires ground plane)",
                results = listOf(
                    "Length" to "%.2f ft (%.2f m)".format(
                        AntennaCalculations.quarterWaveVerticalFeet(freqValue),
                        AntennaCalculations.quarterWaveVerticalMeters(freqValue)
                    )
                ),
                formula = "234 / freq (MHz)"
            )

            // Full-wave loop
            CalculatorResultCard(
                title = "Full-Wave Loop",
                description = "Total loop circumference",
                results = listOf(
                    "Circumference" to "%.2f ft (%.2f m)".format(
                        AntennaCalculations.fullWaveLoopFeet(freqValue),
                        AntennaCalculations.fullWaveLoopMeters(freqValue)
                    ),
                    "Per side (square)" to "%.2f ft".format(
                        AntennaCalculations.fullWaveLoopFeet(freqValue) / 4
                    )
                ),
                formula = "1005 / freq (MHz)"
            )

            // J-Pole
            val jPole = AntennaCalculations.jPoleDimensionsFeet(freqValue)
            CalculatorResultCard(
                title = "J-Pole Antenna",
                description = "Vertical antenna with J-shaped matching section",
                results = listOf(
                    "Radiator (3/4λ)" to "%.2f ft".format(jPole.first),
                    "Matching stub (1/4λ)" to "%.2f ft".format(jPole.second)
                ),
                formula = "3/4 wave + 1/4 wave stub"
            )

            // Wavelength
            CalculatorResultCard(
                title = "Wavelength",
                description = "Full wavelength at this frequency",
                results = listOf(
                    "Wavelength" to "%.2f m".format(
                        AntennaCalculations.wavelengthMeters(freqValue)
                    )
                ),
                formula = "c / frequency"
            )
        }
    }
}

@Composable
private fun ElectricalCalculatorTab() {
    var voltage by remember { mutableStateOf("") }
    var current by remember { mutableStateOf("") }
    var resistance by remember { mutableStateOf("") }

    val v = voltage.toDoubleOrNull()
    val i = current.toDoubleOrNull()
    val r = resistance.toDoubleOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ohm's Law Calculator",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Enter any two values to calculate the third",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = voltage,
            onValueChange = { voltage = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Voltage (V)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        OutlinedTextField(
            value = current,
            onValueChange = { current = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Current (A)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        OutlinedTextField(
            value = resistance,
            onValueChange = { resistance = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Resistance (Ω)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        // Calculate results
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Results",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                when {
                    i != null && r != null -> {
                        val calcV = AntennaCalculations.voltage(i, r)
                        ResultRow("Voltage (V = I × R)", "%.3f V".format(calcV))
                        ResultRow("Power (P = I × R × I)", "%.3f W".format(calcV * i))
                    }
                    v != null && r != null -> {
                        val calcI = AntennaCalculations.current(v, r)
                        ResultRow("Current (I = V / R)", "%.3f A".format(calcI))
                        ResultRow("Power (P = V² / R)", "%.3f W".format(AntennaCalculations.powerFromVoltage(v, r)))
                    }
                    v != null && i != null -> {
                        val calcR = AntennaCalculations.resistance(v, i)
                        ResultRow("Resistance (R = V / I)", "%.3f Ω".format(calcR))
                        ResultRow("Power (P = V × I)", "%.3f W".format(AntennaCalculations.power(v, i)))
                    }
                    else -> {
                        Text(
                            text = "Enter at least two values",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Formulas reference
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Ohm's Law Formulas",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text("V = I × R", style = MaterialTheme.typography.bodySmall)
                Text("I = V / R", style = MaterialTheme.typography.bodySmall)
                Text("R = V / I", style = MaterialTheme.typography.bodySmall)
                Text("P = V × I = I² × R = V² / R", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun PowerCalculatorTab() {
    var watts by remember { mutableStateOf("100") }
    val wattsValue = watts.toDoubleOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Power Conversion",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = watts,
            onValueChange = { watts = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Power (Watts)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        if (wattsValue > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResultRow("Watts", "%.3f W".format(wattsValue))
                    ResultRow("Milliwatts", "%.1f mW".format(wattsValue * 1000))
                    ResultRow("dBm", "%.2f dBm".format(AntennaCalculations.wattsTodBm(wattsValue)))
                    ResultRow("dBW", "%.2f dBW".format(AntennaCalculations.wattsTodBW(wattsValue)))
                }
            }
        }

        // Common power levels reference
        Text(
            text = "Common Power Levels",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PowerLevelRow("QRP Limit", "5 W", "37.0 dBm")
                PowerLevelRow("Mobile typical", "50 W", "47.0 dBm")
                PowerLevelRow("Base station", "100 W", "50.0 dBm")
                PowerLevelRow("Legal limit (US)", "1500 W", "61.8 dBm")
            }
        }
    }
}

@Composable
private fun SwrCalculatorTab() {
    var forwardPower by remember { mutableStateOf("100") }
    var reflectedPower by remember { mutableStateOf("5") }

    val forward = forwardPower.toDoubleOrNull() ?: 0.0
    val reflected = reflectedPower.toDoubleOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "SWR Calculator",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Calculate SWR from forward and reflected power",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = forwardPower,
            onValueChange = { forwardPower = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Forward Power (W)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        OutlinedTextField(
            value = reflectedPower,
            onValueChange = { reflectedPower = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Reflected Power (W)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        if (forward > 0 && reflected >= 0 && reflected < forward) {
            val swr = AntennaCalculations.swr(forward, reflected)
            val returnLoss = AntennaCalculations.returnLoss(swr)
            val reflCoef = AntennaCalculations.reflectionCoefficient(swr)
            val powerDelivered = forward - reflected

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        swr < 1.5 -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        swr < 2.0 -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        swr < 3.0 -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                        else -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Results",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    ResultRow("SWR", "%.2f : 1".format(swr))
                    ResultRow("Return Loss", "%.2f dB".format(returnLoss))
                    ResultRow("Reflection Coefficient", "%.3f".format(reflCoef))
                    ResultRow("Power Delivered", "%.1f W (%.1f%%)".format(
                        powerDelivered,
                        (powerDelivered / forward) * 100
                    ))
                    ResultRow("Power Lost", "%.1f W (%.1f%%)".format(
                        reflected,
                        (reflected / forward) * 100
                    ))
                }
            }
        }

        // SWR reference
        Text(
            text = "SWR Reference",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SwrReferenceRow("1.0:1", "Perfect match", "0%")
                SwrReferenceRow("1.5:1", "Excellent", "4%")
                SwrReferenceRow("2.0:1", "Good", "11%")
                SwrReferenceRow("3.0:1", "Acceptable", "25%")
                SwrReferenceRow("5.0:1", "Poor", "44%")
                SwrReferenceRow("10.0:1", "Very poor", "67%")
            }
        }
    }
}

@Composable
private fun CalculatorResultCard(
    title: String,
    description: String,
    results: List<Pair<String, String>>,
    formula: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            results.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                text = "Formula: $formula",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PowerLevelRow(name: String, watts: String, dbm: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(text = watts, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(60.dp))
        Text(text = dbm, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(80.dp))
    }
}

@Composable
private fun SwrReferenceRow(swr: String, quality: String, loss: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = swr, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(60.dp))
        Text(text = quality, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(text = "~$loss loss", style = MaterialTheme.typography.bodySmall)
    }
}

package com.hamhub.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Simple bar chart component for displaying categorical data.
 */
@Composable
fun SimpleBarChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    maxBars: Int = 10
) {
    val displayData = data.entries.take(maxBars)
    val maxValue = displayData.maxOfOrNull { it.value } ?: 1

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        displayData.forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(48.dp),
                    textAlign = TextAlign.End
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = value.toFloat() / maxValue.toFloat())
                            .clip(RoundedCornerShape(4.dp))
                            .background(barColor)
                    )
                }
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(36.dp)
                )
            }
        }
    }
}

/**
 * Simple horizontal bar chart with colored segments.
 */
@Composable
fun HorizontalBarChart(
    data: Map<String, Int>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    maxItems: Int = 6
) {
    val displayData = data.entries.take(maxItems)
    val total = displayData.sumOf { it.value }.coerceAtLeast(1)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Stacked bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            displayData.forEachIndexed { index, (_, value) ->
                val fraction = value.toFloat() / total.toFloat()
                val color = colors[index % colors.size]
                Box(
                    modifier = Modifier
                        .weight(fraction.coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(color)
                )
            }
        }

        // Legend
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            displayData.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    row.forEachIndexed { idx, (label, value) ->
                        val globalIndex = displayData.indexOf(label to value)
                        val color = colors[globalIndex % colors.size]
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(color)
                            )
                            Text(
                                text = "$label ($value)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Simple line/area chart for time series data.
 */
@Composable
fun SimpleLineChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    val entries = data.entries.toList()
    val maxValue = entries.maxOfOrNull { it.value }?.toFloat() ?: 1f
    val minValue = 0f

    Column(modifier = modifier.fillMaxWidth()) {
        // Chart area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (entries.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    entries.forEach { (_, value) ->
                        val heightFraction = if (maxValue > 0) {
                            (value - minValue) / (maxValue - minValue)
                        } else {
                            0f
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(heightFraction.coerceIn(0.05f, 1f))
                                .padding(horizontal = 2.dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(lineColor.copy(alpha = 0.7f))
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // X-axis labels (show first, middle, last)
        if (entries.size >= 3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMonthLabel(entries.first().key),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = formatMonthLabel(entries[entries.size / 2].key),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = formatMonthLabel(entries.last().key),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

/**
 * Hour distribution chart (24-hour clock).
 */
@Composable
fun HourDistributionChart(
    data: Map<Int, Int>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.tertiary
) {
    val maxValue = data.values.maxOrNull() ?: 1

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                (0..23).forEach { hour ->
                    val value = data[hour] ?: 0
                    val heightFraction = if (maxValue > 0) {
                        value.toFloat() / maxValue.toFloat()
                    } else {
                        0f
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(heightFraction.coerceIn(0.02f, 1f))
                            .padding(horizontal = 1.dp)
                            .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                            .background(
                                if (value > 0) barColor.copy(alpha = 0.8f)
                                else barColor.copy(alpha = 0.1f)
                            )
                    )
                }
            }
        }

        // Hour labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("00", style = MaterialTheme.typography.labelSmall)
            Text("06", style = MaterialTheme.typography.labelSmall)
            Text("12", style = MaterialTheme.typography.labelSmall)
            Text("18", style = MaterialTheme.typography.labelSmall)
            Text("23", style = MaterialTheme.typography.labelSmall)
        }
    }
}

private fun formatMonthLabel(yearMonth: String): String {
    return try {
        val parts = yearMonth.split("-")
        if (parts.size == 2) {
            val month = when (parts[1]) {
                "01" -> "Jan"
                "02" -> "Feb"
                "03" -> "Mar"
                "04" -> "Apr"
                "05" -> "May"
                "06" -> "Jun"
                "07" -> "Jul"
                "08" -> "Aug"
                "09" -> "Sep"
                "10" -> "Oct"
                "11" -> "Nov"
                "12" -> "Dec"
                else -> parts[1]
            }
            "$month '${parts[0].takeLast(2)}"
        } else {
            yearMonth
        }
    } catch (e: Exception) {
        yearMonth
    }
}

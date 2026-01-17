package com.hamhub.app.domain.util

import com.hamhub.app.data.local.entity.QsoEntity
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Parser for ADIF (Amateur Data Interchange Format) files.
 * Supports ADIF 3.1.4 specification.
 */
object AdifParser {

    private val FIELD_PATTERN = Regex("<([^:>]+):([0-9]+)(?::([^>]+))?>([^<]*)", RegexOption.IGNORE_CASE)
    private val HEADER_END_PATTERN = Regex("<EOH>", RegexOption.IGNORE_CASE)
    private val RECORD_END_PATTERN = Regex("<EOR>", RegexOption.IGNORE_CASE)

    data class ParseResult(
        val qsos: List<QsoEntity>,
        val errors: List<String>,
        val totalRecords: Int,
        val successfulRecords: Int
    )

    /**
     * Parse an ADIF string and return QSO entities.
     */
    fun parse(adifContent: String): ParseResult {
        val errors = mutableListOf<String>()
        val qsos = mutableListOf<QsoEntity>()

        // Remove header if present
        val content = HEADER_END_PATTERN.find(adifContent)?.let {
            adifContent.substring(it.range.last + 1)
        } ?: adifContent

        // Split into records
        val records = RECORD_END_PATTERN.split(content)
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        records.forEachIndexed { index, record ->
            try {
                val fields = parseFields(record)
                val qso = fieldsToQso(fields)
                if (qso != null) {
                    qsos.add(qso)
                } else {
                    errors.add("Record ${index + 1}: Missing required fields (date, time, callsign)")
                }
            } catch (e: Exception) {
                errors.add("Record ${index + 1}: ${e.message}")
            }
        }

        return ParseResult(
            qsos = qsos,
            errors = errors,
            totalRecords = records.size,
            successfulRecords = qsos.size
        )
    }

    private fun parseFields(record: String): Map<String, String> {
        val fields = mutableMapOf<String, String>()

        FIELD_PATTERN.findAll(record).forEach { match ->
            val fieldName = match.groupValues[1].uppercase()
            val length = match.groupValues[2].toIntOrNull() ?: 0
            val value = match.groupValues[4].take(length)
            fields[fieldName] = value
        }

        return fields
    }

    private fun fieldsToQso(fields: Map<String, String>): QsoEntity? {
        val callsign = fields["CALL"] ?: return null
        val qsoDate = fields["QSO_DATE"] ?: return null
        val timeOn = fields["TIME_ON"] ?: fields["TIME_OFF"] ?: return null

        // Parse date from YYYYMMDD format
        val date = try {
            val year = qsoDate.substring(0, 4)
            val month = qsoDate.substring(4, 6)
            val day = qsoDate.substring(6, 8)
            "$year-$month-$day"
        } catch (e: Exception) {
            return null
        }

        // Parse time from HHMM or HHMMSS format
        val time = try {
            val hour = timeOn.substring(0, 2)
            val minute = timeOn.substring(2, 4)
            "$hour:$minute"
        } catch (e: Exception) {
            "00:00"
        }

        val band = fields["BAND"] ?: inferBandFromFrequency(fields["FREQ"])
        val mode = fields["MODE"] ?: "SSB"

        return QsoEntity(
            id = 0,
            date = date,
            timeUtc = time,
            callsign = callsign.uppercase(),
            frequency = fields["FREQ"]?.toDoubleOrNull(),
            band = band ?: "20m",
            mode = normalizeMode(mode),
            rstSent = fields["RST_SENT"],
            rstReceived = fields["RST_RCVD"],
            name = fields["NAME"],
            qth = fields["QTH"],
            gridSquare = fields["GRIDSQUARE"]?.uppercase(),
            country = fields["COUNTRY"],
            dxcc = fields["DXCC"]?.toIntOrNull(),
            state = fields["STATE"]?.uppercase(),
            power = fields["TX_PWR"]?.toDoubleOrNull()?.toInt(),
            notes = fields["COMMENT"] ?: fields["NOTES"],
            qslSent = fields["QSL_SENT"]?.uppercase() == "Y",
            qslReceived = fields["QSL_RCVD"]?.uppercase() == "Y",
            contestName = fields["CONTEST_ID"],
            contestExchange = fields["SRX_STRING"] ?: fields["STX_STRING"],
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    private fun inferBandFromFrequency(freqStr: String?): String? {
        val freq = freqStr?.toDoubleOrNull() ?: return null
        return when {
            freq in 1.8..2.0 -> "160m"
            freq in 3.5..4.0 -> "80m"
            freq in 5.3..5.5 -> "60m"
            freq in 7.0..7.3 -> "40m"
            freq in 10.1..10.15 -> "30m"
            freq in 14.0..14.35 -> "20m"
            freq in 18.068..18.168 -> "17m"
            freq in 21.0..21.45 -> "15m"
            freq in 24.89..24.99 -> "12m"
            freq in 28.0..29.7 -> "10m"
            freq in 50.0..54.0 -> "6m"
            freq in 144.0..148.0 -> "2m"
            freq in 420.0..450.0 -> "70cm"
            else -> null
        }
    }

    private fun normalizeMode(mode: String): String {
        return when (mode.uppercase()) {
            "USB", "LSB" -> "SSB"
            "PHONE" -> "SSB"
            "CW" -> "CW"
            "FT8" -> "FT8"
            "FT4" -> "FT4"
            "JS8" -> "JS8"
            "RTTY" -> "RTTY"
            "PSK31", "PSK" -> "PSK31"
            "FM" -> "FM"
            "AM" -> "AM"
            "SSB" -> "SSB"
            else -> mode.uppercase()
        }
    }
}

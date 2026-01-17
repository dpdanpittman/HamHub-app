package com.hamhub.app.domain.util

import com.hamhub.app.data.local.database.entity.QsoEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Exporter for ADIF (Amateur Data Interchange Format) files.
 * Generates ADIF 3.1.4 compliant output.
 */
object AdifExporter {

    private const val ADIF_VERSION = "3.1.4"
    private const val PROGRAM_ID = "HamHub"
    private const val PROGRAM_VERSION = "1.0.0"

    /**
     * Export a list of QSOs to ADIF format.
     */
    fun export(qsos: List<QsoEntity>): String {
        val builder = StringBuilder()

        // Write header
        builder.appendLine(generateHeader())
        builder.appendLine()

        // Write each QSO record
        qsos.forEach { qso ->
            builder.appendLine(generateRecord(qso))
            builder.appendLine()
        }

        return builder.toString()
    }

    /**
     * Export QSOs to ADIF with optional filters.
     */
    fun exportFiltered(
        qsos: List<QsoEntity>,
        bandFilter: String? = null,
        modeFilter: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): String {
        var filtered = qsos

        bandFilter?.let { band ->
            filtered = filtered.filter { it.band.equals(band, ignoreCase = true) }
        }

        modeFilter?.let { mode ->
            filtered = filtered.filter { it.mode.equals(mode, ignoreCase = true) }
        }

        startDate?.let { start ->
            filtered = filtered.filter { it.date >= start }
        }

        endDate?.let { end ->
            filtered = filtered.filter { it.date <= end }
        }

        return export(filtered)
    }

    private fun generateHeader(): String {
        val now = LocalDateTime.now()
        val timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd HHmmss"))

        return buildString {
            appendLine("ADIF Export from $PROGRAM_ID")
            appendLine("Generated: $timestamp")
            appendLine()
            append(formatField("ADIF_VER", ADIF_VERSION))
            append(formatField("PROGRAMID", PROGRAM_ID))
            append(formatField("PROGRAMVERSION", PROGRAM_VERSION))
            append("<EOH>")
        }
    }

    private fun generateRecord(qso: QsoEntity): String {
        return buildString {
            // Required fields
            append(formatField("CALL", qso.callsign))
            append(formatField("QSO_DATE", formatDate(qso.date)))
            append(formatField("TIME_ON", formatTime(qso.timeUtc)))
            append(formatField("BAND", qso.band.lowercase()))
            append(formatField("MODE", qso.mode.uppercase()))

            // Optional fields
            qso.frequency?.let { append(formatField("FREQ", it.toString())) }
            qso.rstSent?.let { append(formatField("RST_SENT", it)) }
            qso.rstReceived?.let { append(formatField("RST_RCVD", it)) }
            qso.name?.let { append(formatField("NAME", it)) }
            qso.qth?.let { append(formatField("QTH", it)) }
            qso.gridSquare?.let { append(formatField("GRIDSQUARE", it.uppercase())) }
            qso.country?.let { append(formatField("COUNTRY", it)) }
            qso.dxcc?.let { append(formatField("DXCC", it.toString())) }
            qso.state?.let { append(formatField("STATE", it.uppercase())) }
            qso.power?.let { append(formatField("TX_PWR", it.toString())) }
            qso.notes?.let { append(formatField("COMMENT", it)) }

            // QSL status
            if (qso.qslSent) append(formatField("QSL_SENT", "Y"))
            if (qso.qslReceived) append(formatField("QSL_RCVD", "Y"))

            // Contest fields
            qso.contestName?.let { append(formatField("CONTEST_ID", it)) }
            qso.contestExchange?.let { append(formatField("SRX_STRING", it)) }

            append("<EOR>")
        }
    }

    private fun formatField(name: String, value: String): String {
        return "<${name.uppercase()}:${value.length}>$value "
    }

    /**
     * Convert date from YYYY-MM-DD to YYYYMMDD format.
     */
    private fun formatDate(date: String): String {
        return date.replace("-", "")
    }

    /**
     * Convert time from HH:MM to HHMM format.
     */
    private fun formatTime(time: String): String {
        return time.replace(":", "")
    }

    /**
     * Generate a filename for the export.
     */
    fun generateFilename(prefix: String = "hamhub_export"): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        return "${prefix}_$timestamp.adi"
    }
}

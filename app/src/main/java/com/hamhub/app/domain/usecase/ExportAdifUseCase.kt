package com.hamhub.app.domain.usecase

import com.hamhub.app.data.repository.QsoRepository
import com.hamhub.app.domain.util.AdifExporter
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class ExportResult(
    val content: String,
    val filename: String,
    val qsoCount: Int
)

class ExportAdifUseCase @Inject constructor(
    private val qsoRepository: QsoRepository
) {
    suspend operator fun invoke(
        bandFilter: String? = null,
        modeFilter: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): ExportResult {
        val allQsos = qsoRepository.getAllQsos().first()

        val content = AdifExporter.exportFiltered(
            qsos = allQsos,
            bandFilter = bandFilter,
            modeFilter = modeFilter,
            startDate = startDate,
            endDate = endDate
        )

        // Count exported QSOs
        var filtered = allQsos
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

        return ExportResult(
            content = content,
            filename = AdifExporter.generateFilename(),
            qsoCount = filtered.size
        )
    }
}

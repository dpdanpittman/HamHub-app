package com.hamhub.app.domain.usecase

import com.hamhub.app.data.repository.QsoRepository
import com.hamhub.app.domain.util.AdifParser
import javax.inject.Inject

data class ImportResult(
    val totalRecords: Int,
    val importedRecords: Int,
    val duplicatesSkipped: Int,
    val errors: List<String>
)

class ImportAdifUseCase @Inject constructor(
    private val qsoRepository: QsoRepository
) {
    suspend operator fun invoke(
        adifContent: String,
        skipDuplicates: Boolean = true
    ): ImportResult {
        val parseResult = AdifParser.parse(adifContent)
        var importedCount = 0
        var duplicatesSkipped = 0
        val errors = parseResult.errors.toMutableList()

        parseResult.qsos.forEach { qso ->
            try {
                if (skipDuplicates) {
                    val existing = qsoRepository.checkDuplicate(
                        callsign = qso.callsign,
                        band = qso.band,
                        mode = qso.mode,
                        date = qso.date
                    )
                    if (existing != null) {
                        duplicatesSkipped++
                        return@forEach
                    }
                }
                qsoRepository.insertQso(qso)
                importedCount++
            } catch (e: Exception) {
                errors.add("Failed to import ${qso.callsign}: ${e.message}")
            }
        }

        return ImportResult(
            totalRecords = parseResult.totalRecords,
            importedRecords = importedCount,
            duplicatesSkipped = duplicatesSkipped,
            errors = errors
        )
    }
}

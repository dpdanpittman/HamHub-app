package com.hamhub.app.data.repository

import com.hamhub.app.data.local.database.dao.QsoDao
import com.hamhub.app.data.local.entity.QsoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QsoRepository @Inject constructor(
    private val qsoDao: QsoDao
) {
    fun getAllQsos(): Flow<List<QsoEntity>> = qsoDao.getAllQsos()

    suspend fun getQsoById(id: Long): QsoEntity? = qsoDao.getQsoById(id)

    suspend fun getQsosFiltered(
        band: String? = null,
        mode: String? = null,
        callsign: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<QsoEntity> = qsoDao.getQsosFiltered(band, mode, callsign, startDate, endDate, limit, offset)

    suspend fun getQsoCount(): Int = qsoDao.getQsoCount()

    fun getQsoCountFlow(): Flow<Int> = qsoDao.getQsoCountFlow()

    suspend fun getUniqueCallsignCount(): Int = qsoDao.getUniqueCallsignCount()

    suspend fun getUniqueCountryCount(): Int = qsoDao.getUniqueCountryCount()

    suspend fun getUniqueGridCount(): Int = qsoDao.getUniqueGridCount()

    suspend fun getConfirmedQsoCount(): Int = qsoDao.getConfirmedQsoCount()

    suspend fun findDuplicate(
        callsign: String,
        date: String,
        band: String,
        mode: String
    ): QsoEntity? = qsoDao.findDuplicate(callsign, date, band, mode)

    suspend fun insertQso(qso: QsoEntity): Long = qsoDao.insertQso(qso)

    suspend fun insertQsos(qsos: List<QsoEntity>): List<Long> = qsoDao.insertQsos(qsos)

    suspend fun updateQso(qso: QsoEntity) = qsoDao.updateQso(qso)

    suspend fun deleteQso(qso: QsoEntity) = qsoDao.deleteQso(qso)

    suspend fun deleteQsoById(id: Long) = qsoDao.deleteQsoById(id)

    suspend fun deleteAllQsos() = qsoDao.deleteAllQsos()

    suspend fun getQsosForExport(
        startDate: String? = null,
        endDate: String? = null,
        band: String? = null,
        mode: String? = null
    ): List<QsoEntity> = qsoDao.getQsosForExport(startDate, endDate, band, mode)
}

package com.hamhub.app.data.repository

import com.hamhub.app.data.local.database.dao.QsoDao
import com.hamhub.app.data.local.database.entity.QsoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QsoRepository @Inject constructor(
    private val qsoDao: QsoDao
) {
    fun getAllQsos(): Flow<List<QsoEntity>> = qsoDao.getAllQsos()

    fun getQsoById(id: Long): Flow<QsoEntity?> = qsoDao.getQsoById(id)

    fun searchQsos(query: String): Flow<List<QsoEntity>> = qsoDao.searchQsos(query)

    fun getQsosByBand(band: String): Flow<List<QsoEntity>> = qsoDao.getQsosByBand(band)

    fun getQsosByMode(mode: String): Flow<List<QsoEntity>> = qsoDao.getQsosByMode(mode)

    fun getQsosByDateRange(startDate: String, endDate: String): Flow<List<QsoEntity>> =
        qsoDao.getQsosByDateRange(startDate, endDate)

    fun getQsosByBandAndMode(band: String, mode: String): Flow<List<QsoEntity>> =
        qsoDao.getQsosByBandAndMode(band, mode)

    suspend fun insertQso(qso: QsoEntity): Long = qsoDao.insertQso(qso)

    suspend fun updateQso(qso: QsoEntity) = qsoDao.updateQso(qso)

    suspend fun deleteQso(qso: QsoEntity) = qsoDao.deleteQso(qso)

    suspend fun deleteQsoById(id: Long) = qsoDao.deleteQsoById(id)

    suspend fun getTotalQsoCount(): Int = qsoDao.getTotalQsoCount()

    suspend fun getUniqueCallsignCount(): Int = qsoDao.getUniqueCallsignCount()

    suspend fun getUniqueCountryCount(): Int = qsoDao.getUniqueCountryCount()

    suspend fun getUniqueGridCount(): Int = qsoDao.getUniqueGridCount()

    suspend fun checkDuplicate(
        callsign: String,
        band: String,
        mode: String,
        date: String
    ): QsoEntity? = qsoDao.checkDuplicate(callsign, band, mode, date)
}

package com.hamhub.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "qsos",
    indices = [
        Index(value = ["date"]),
        Index(value = ["callsign"]),
        Index(value = ["band"]),
        Index(value = ["mode"]),
        Index(value = ["country"]),
        Index(value = ["dxcc"]),
        Index(value = ["state"]),
        Index(value = ["grid_square"])
    ]
)
data class QsoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "date")
    val date: String, // YYYY-MM-DD

    @ColumnInfo(name = "time_utc")
    val timeUtc: String, // HH:MM or HH:MM:SS

    @ColumnInfo(name = "frequency")
    val frequency: Double? = null,

    @ColumnInfo(name = "band")
    val band: String,

    @ColumnInfo(name = "mode")
    val mode: String,

    @ColumnInfo(name = "callsign")
    val callsign: String,

    @ColumnInfo(name = "rst_sent")
    val rstSent: String? = null,

    @ColumnInfo(name = "rst_received")
    val rstReceived: String? = null,

    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "qth")
    val qth: String? = null,

    @ColumnInfo(name = "grid_square")
    val gridSquare: String? = null,

    @ColumnInfo(name = "country")
    val country: String? = null,

    @ColumnInfo(name = "dxcc")
    val dxcc: Int? = null,

    @ColumnInfo(name = "state")
    val state: String? = null,

    @ColumnInfo(name = "power")
    val power: Int? = null,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "qsl_sent")
    val qslSent: Boolean = false,

    @ColumnInfo(name = "qsl_received")
    val qslReceived: Boolean = false,

    @ColumnInfo(name = "contest_name")
    val contestName: String? = null,

    @ColumnInfo(name = "contest_exchange")
    val contestExchange: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

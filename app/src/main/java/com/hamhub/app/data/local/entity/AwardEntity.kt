package com.hamhub.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "awards",
    indices = [
        Index(value = ["award_type"]),
        Index(value = ["entity"]),
        Index(value = ["qso_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = QsoEntity::class,
            parentColumns = ["id"],
            childColumns = ["qso_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AwardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "award_type")
    val awardType: String, // "DXCC", "WAS", "GRID"

    @ColumnInfo(name = "entity")
    val entity: String, // Country code, state abbreviation, or grid square

    @ColumnInfo(name = "band")
    val band: String? = null,

    @ColumnInfo(name = "mode")
    val mode: String? = null,

    @ColumnInfo(name = "confirmed")
    val confirmed: Boolean = false,

    @ColumnInfo(name = "qso_id")
    val qsoId: Long,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

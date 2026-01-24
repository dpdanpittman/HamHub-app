package com.hamhub.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "spotted_callsigns",
    foreignKeys = [
        ForeignKey(
            entity = SpotterListEntity::class,
            parentColumns = ["id"],
            childColumns = ["list_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["list_id"]),
        Index(value = ["callsign"])
    ]
)
data class SpottedCallsignEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "list_id")
    val listId: Long,

    @ColumnInfo(name = "callsign")
    val callsign: String,

    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "grid_square")
    val gridSquare: String? = null,

    @ColumnInfo(name = "location")
    val location: String? = null,

    @ColumnInfo(name = "operator_class")
    val operatorClass: String? = null,

    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis()
)

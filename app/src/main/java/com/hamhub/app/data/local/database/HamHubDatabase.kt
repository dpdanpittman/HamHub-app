package com.hamhub.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hamhub.app.data.local.database.dao.AwardsDao
import com.hamhub.app.data.local.database.dao.QsoDao
import com.hamhub.app.data.local.database.dao.SettingsDao
import com.hamhub.app.data.local.entity.AwardEntity
import com.hamhub.app.data.local.entity.QsoEntity
import com.hamhub.app.data.local.entity.SettingsEntity

@Database(
    entities = [
        QsoEntity::class,
        SettingsEntity::class,
        AwardEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class HamHubDatabase : RoomDatabase() {

    abstract fun qsoDao(): QsoDao
    abstract fun settingsDao(): SettingsDao
    abstract fun awardsDao(): AwardsDao

    companion object {
        const val DATABASE_NAME = "hamhub_database"
    }
}

package com.hamhub.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hamhub.app.data.local.database.dao.AwardsDao
import com.hamhub.app.data.local.database.dao.QsoDao
import com.hamhub.app.data.local.database.dao.SettingsDao
import com.hamhub.app.data.local.database.dao.SpotterDao
import com.hamhub.app.data.local.entity.AwardEntity
import com.hamhub.app.data.local.entity.QsoEntity
import com.hamhub.app.data.local.entity.SettingsEntity
import com.hamhub.app.data.local.entity.SpotterListEntity
import com.hamhub.app.data.local.entity.SpottedCallsignEntity

@Database(
    entities = [
        QsoEntity::class,
        SettingsEntity::class,
        AwardEntity::class,
        SpotterListEntity::class,
        SpottedCallsignEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class HamHubDatabase : RoomDatabase() {

    abstract fun qsoDao(): QsoDao
    abstract fun settingsDao(): SettingsDao
    abstract fun awardsDao(): AwardsDao
    abstract fun spotterDao(): SpotterDao

    companion object {
        const val DATABASE_NAME = "hamhub_database"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create spotter_lists table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS spotter_lists (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        description TEXT,
                        created_at INTEGER NOT NULL
                    )
                """)

                // Create spotted_callsigns table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS spotted_callsigns (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        list_id INTEGER NOT NULL,
                        callsign TEXT NOT NULL,
                        name TEXT,
                        grid_square TEXT,
                        location TEXT,
                        operator_class TEXT,
                        added_at INTEGER NOT NULL,
                        FOREIGN KEY(list_id) REFERENCES spotter_lists(id) ON DELETE CASCADE
                    )
                """)

                // Create indices for spotted_callsigns
                database.execSQL("CREATE INDEX IF NOT EXISTS index_spotted_callsigns_list_id ON spotted_callsigns(list_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_spotted_callsigns_callsign ON spotted_callsigns(callsign)")
            }
        }
    }
}

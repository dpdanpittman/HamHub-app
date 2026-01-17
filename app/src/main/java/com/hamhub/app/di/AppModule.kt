package com.hamhub.app.di

import android.content.Context
import androidx.room.Room
import com.hamhub.app.data.local.database.HamHubDatabase
import com.hamhub.app.data.local.database.dao.AwardsDao
import com.hamhub.app.data.local.database.dao.QsoDao
import com.hamhub.app.data.local.database.dao.SettingsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): HamHubDatabase {
        return Room.databaseBuilder(
            context,
            HamHubDatabase::class.java,
            HamHubDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideQsoDao(database: HamHubDatabase): QsoDao {
        return database.qsoDao()
    }

    @Provides
    @Singleton
    fun provideSettingsDao(database: HamHubDatabase): SettingsDao {
        return database.settingsDao()
    }

    @Provides
    @Singleton
    fun provideAwardsDao(database: HamHubDatabase): AwardsDao {
        return database.awardsDao()
    }
}

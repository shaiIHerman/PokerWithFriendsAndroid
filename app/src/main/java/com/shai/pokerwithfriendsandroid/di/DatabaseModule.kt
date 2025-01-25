package com.shai.pokerwithfriendsandroid.di

import android.content.Context
import androidx.room.Room
import com.shai.pokerwithfriendsandroid.data.local.db.AppDatabase
import com.shai.pokerwithfriendsandroid.data.local.db.daos.GameDao
import com.shai.pokerwithfriendsandroid.data.local.db.daos.SyncInfoDao
import com.shai.pokerwithfriendsandroid.data.local.db.daos.TournamentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "tournaments_database"
        ).fallbackToDestructiveMigration() // Optional: Useful for migrations
            .build()
    }

    @Provides
    @Singleton
    fun provideTournamentDao(appDatabase: AppDatabase): TournamentDao {
        return appDatabase.tournamentDao()
    }

    @Provides
    @Singleton
    fun provideSyncInfoDao(appDatabase: AppDatabase): SyncInfoDao {
        return appDatabase.SyncInfoDao()
    }

    @Provides
    @Singleton
    fun provideGameDao(appDatabase: AppDatabase): GameDao {
        return appDatabase.gameDao()
    }
}

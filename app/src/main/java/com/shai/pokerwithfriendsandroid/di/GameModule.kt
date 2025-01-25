package com.shai.pokerwithfriendsandroid.di

import com.shai.pokerwithfriendsandroid.data.local.db.daos.GameDao
import com.shai.pokerwithfriendsandroid.data.local.db.daos.SyncInfoDao
import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.remote.FirestoreRealtimeListener
import com.shai.pokerwithfriendsandroid.data.sources.FirestoreGameDataSource
import com.shai.pokerwithfriendsandroid.data.sources.LocalGameDataSource
import com.shai.pokerwithfriendsandroid.data.sources.RemoteGameDataSource
import com.shai.pokerwithfriendsandroid.data.sources.RoomGameDataSource
import com.shai.pokerwithfriendsandroid.domain.repositories.GamesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GameModule {

    @Provides
    fun provideRemoteGameDataSource(firestoreClient: FireStoreClient): RemoteGameDataSource {
        return FirestoreGameDataSource(firestoreClient)
    }

    @Provides
    fun provideLocalGameDataSource(gameDao: GameDao): LocalGameDataSource {
        return RoomGameDataSource(gameDao)
    }

    @Provides
    fun provideGameRepository(
        remoteGameDataSource: RemoteGameDataSource,
        localGameDataSource: LocalGameDataSource,
        firestoreClient: FireStoreClient,
        firestoreListener: FirestoreRealtimeListener,
        syncInfoDao: SyncInfoDao
    ): GamesRepository {
        return GamesRepository(
            remoteGameDataSource,
            localGameDataSource,
            firestoreClient,
            firestoreListener,
            syncInfoDao
        )
    }
}

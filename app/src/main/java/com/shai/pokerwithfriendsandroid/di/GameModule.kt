package com.shai.pokerwithfriendsandroid.di

import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.remote.FirestoreRealtimeListener
import com.shai.pokerwithfriendsandroid.data.sources.FirestoreGameDataSource
import com.shai.pokerwithfriendsandroid.data.sources.RemoteGameDataSource
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
    fun provideGameRepository(
        remoteGameDataSource: RemoteGameDataSource,
        firestoreClient: FireStoreClient,
        firestoreListener: FirestoreRealtimeListener
    ): GamesRepository {
        return GamesRepository(remoteGameDataSource, firestoreClient, firestoreListener)
    }
}

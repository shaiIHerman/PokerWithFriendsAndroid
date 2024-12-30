package com.shai.pokerwithfriendsandroid.di


import com.shai.pokerwithfriendsandroid.data.remote.FireStoreAPI
import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.remote.FirestoreRealtimeListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DBModule {

    @Provides
    @Singleton
    fun providesFireStoreAPI(): FireStoreAPI {
        return FireStoreAPI()
    }

    @Provides
    @Singleton
    fun providesFireStoreClient(fireStoreAPI: FireStoreAPI): FireStoreClient {
        return FireStoreClient(fireStoreAPI)
    }

    @Provides
    @Singleton
    fun providesFireStoreListener(): FirestoreRealtimeListener {
        return FirestoreRealtimeListener()
    }
}
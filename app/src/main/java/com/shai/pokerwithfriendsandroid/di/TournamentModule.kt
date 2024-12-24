package com.shai.pokerwithfriendsandroid.di

import com.shai.pokerwithfriendsandroid.data.local.db.daos.SyncInfoDao
import com.shai.pokerwithfriendsandroid.data.local.db.daos.TournamentDao
import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.sources.FirestoreTournamentDataSource
import com.shai.pokerwithfriendsandroid.data.sources.LocalTournamentDataSource
import com.shai.pokerwithfriendsandroid.data.sources.RemoteTournamentDataSource
import com.shai.pokerwithfriendsandroid.data.sources.RoomTournamentDataSource
import com.shai.pokerwithfriendsandroid.domain.repositories.TournamentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TournamentModule {

    @Provides
    fun provideRemoteTournamentDataSource(firestoreClient: FireStoreClient): RemoteTournamentDataSource {
        return FirestoreTournamentDataSource(firestoreClient)
    }

    @Provides
    fun provideLocalTournamentDataSource(tournamentDao: TournamentDao): LocalTournamentDataSource {
        return RoomTournamentDataSource(tournamentDao)
    }

    @Provides
    fun provideTournamentRepository(
        remoteTournamentDataSource: RemoteTournamentDataSource,
        localTournamentDataSource: LocalTournamentDataSource,
        tournamentDao: TournamentDao,
        firestoreClient: FireStoreClient,
        syncInfoDao: SyncInfoDao
    ): TournamentRepository {
        return TournamentRepository(
            remoteTournamentDataSource,
            localTournamentDataSource,
            tournamentDao,
            firestoreClient,
            syncInfoDao
        )
    }
}

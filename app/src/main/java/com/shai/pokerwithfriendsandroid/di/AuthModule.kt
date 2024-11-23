package com.shai.pokerwithfriendsandroid.di

import com.google.firebase.auth.FirebaseAuth
import com.shai.pokerwithfriendsandroid.auth.AuthService
import com.shai.pokerwithfriendsandroid.auth.FirebaseAuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)  // Ensures this is available throughout the app
object AuthModule {

    @Provides
    fun provideAuthService(firebaseAuth: FirebaseAuth): AuthService {
        return FirebaseAuthService(firebaseAuth)
    }

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}

package com.shai.pokerwithfriendsandroid.repositories

import com.google.firebase.auth.FirebaseUser
import com.shai.pokerwithfriendsandroid.auth.AuthService
import com.shai.pokerwithfriendsandroid.db.remote.ApiOperation
import com.shai.pokerwithfriendsandroid.db.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.db.remote.safeApiCall
import javax.inject.Inject

class UserRepository @Inject constructor(private val authService: AuthService, private val fireStoreClient: FireStoreClient) {
    suspend fun registerNewUser(email: String, password: String, name: String): ApiOperation<Void> {
        val authUser = authService.signUpWithEmail(email, password).toDomainAuthUser()
        return safeApiCall {
            val documentReference = fireStoreClient.createDocument("users", authUser.uid)
            fireStoreClient.setUserDocumentData(documentReference, authUser, name)
        }
    }
}

private fun FirebaseUser?.toDomainAuthUser(): AuthUser {
    return AuthUser(this?.uid ?: "", this?.email ?: "")
}

class AuthUser(val uid: String, val email: String)

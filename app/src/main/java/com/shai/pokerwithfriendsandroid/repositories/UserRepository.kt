package com.shai.pokerwithfriendsandroid.repositories

import com.shai.pokerwithfriendsandroid.auth.AuthService
import com.shai.pokerwithfriendsandroid.db.remote.ApiOperation
import com.shai.pokerwithfriendsandroid.db.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.db.remote.models.User
import com.shai.pokerwithfriendsandroid.db.remote.safeApiCall
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val authService: AuthService, private val fireStoreClient: FireStoreClient
) {

    private var userCache: User? = null
    
    suspend fun registerNewUser(
        email: String,
        password: String,
        name: String
    ): ApiOperation<User?> {
        val authUser = authService.signUpWithEmail(email, password)
        return safeApiCall {
            val documentReference = fireStoreClient.createDocument("users", authUser?.uid ?: "")
            fireStoreClient.setUserDocumentData(documentReference, authUser?.email ?: "", name)
            userCache = fireStoreClient.getDocument<User>(documentReference)
            userCache
        }
    }

    suspend fun loginUser(
        email: String,
        password: String,
    ): ApiOperation<User?> {
        val authUser = authService.loginWithEmail(email, password)
        return safeApiCall {
            userCache = fireStoreClient.getDocument<User>(collectionName = "users", docId = authUser?.uid ?: "")
            userCache
        }
    }
}
